package cn.xwj.usercenter.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import cn.xwj.baselibrary.common.BaseConstants
import cn.xwj.baselibrary.ext.content
import cn.xwj.baselibrary.ext.loadUrl
import cn.xwj.baselibrary.ui.activity.BaseMvpActivity
import cn.xwj.provider.common.RoutePath
import cn.xwj.usercenter.R
import cn.xwj.usercenter.data.protocol.UserInfo
import cn.xwj.usercenter.di.component.DaggerUserComponent
import cn.xwj.usercenter.di.module.UploadModule
import cn.xwj.usercenter.di.module.UserModule
import cn.xwj.usercenter.presenter.UserInfoPresenter
import cn.xwj.usercenter.presenter.view.UserInfoView
import cn.xwj.usercenter.utils.UserPrefsUtils
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.alertview.AlertView
import com.jph.takephoto.app.TakePhoto
import com.jph.takephoto.app.TakePhotoImpl
import com.jph.takephoto.compress.CompressConfig
import com.jph.takephoto.model.InvokeParam
import com.jph.takephoto.model.TContextWrap
import com.jph.takephoto.model.TResult
import com.jph.takephoto.permission.InvokeListener
import com.jph.takephoto.permission.PermissionManager
import com.jph.takephoto.permission.PermissionManager.TPermissionType
import com.jph.takephoto.permission.TakePhotoInvocationHandler
import com.kotlin.base.utils.DateUtils
import com.qiniu.android.storage.UploadManager
import kotlinx.android.synthetic.main.activity_user_info.*
import org.jetbrains.anko.toast
import java.io.File


/**
 * 注册
 */
@Route(path = RoutePath.UserCenter.USER_INFO_PATH)
class UserInfoActivity : BaseMvpActivity<UserInfoPresenter>(), UserInfoView, View.OnClickListener,
        TakePhoto.TakeResultListener, InvokeListener {
    override fun onEditUserResult(result: String) {
        toast(result)
    }


    private lateinit var invokeParam: InvokeParam
    private lateinit var takePhoto: TakePhoto
    private lateinit var mTempFile: File
    private var mLocalFileUrl: String? = null
    private var mRemoteFileUrl: String? = null

    private val uploadManager: UploadManager by lazy { UploadManager() }


    override fun onGetUploadTokenResult(result: String) {
        uploadManager.put(mLocalFileUrl, null, result, { _, _, response ->
            mRemoteFileUrl = BaseConstants.IMAGE_SERVER_ADDRESS + response?.get("hash")
            mUserIconIv.loadUrl(mRemoteFileUrl!!)
        }, null)
    }


    override fun initPerComponent() {
        DaggerUserComponent.builder()
                .userModule(UserModule(this))
                .uploadModule(UploadModule())
                .activityComponent(activityComponent)
                .build()
                .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_info)

        initView()
        initData()
        getTakePhoto()
        takePhoto.onCreate(savedInstanceState)

    }

    private fun initData() {
        val userInfo: UserInfo? = UserPrefsUtils.getUserInfo()
        userInfo?.let {
            with(userInfo) {
                mRemoteFileUrl = userIcon
                mUserIconIv.loadUrl(userIcon)
                mUserNameEt.content = userName
                if (userGender == "0") {
                    mGenderMaleRb.isChecked = true
                } else {
                    mGenderFemaleRb.isChecked = true
                }
                mUserMobileTv.text = userMobile
                mUserSignEt.content = userSign
            }
        }
    }

    private fun initView() {
        mUserIconView.setOnClickListener(this)
        mHeaderBar.getRightView().setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mUserIconView -> showAlertView()
            R.id.mRightTv -> mPresenter.editUser(
                    mRemoteFileUrl!!,
                    mUserNameEt.content,
                    if (mGenderMaleRb.isChecked) "0" else "1",
                    mUserSignEt.content)
        }
    }

    private fun showAlertView() {
        //或者builder模式创建
        AlertView.Builder().setContext(this)
                .setStyle(AlertView.Style.ActionSheet)
                .setTitle("选择操作")
                .setMessage(null)
                .setCancelText("取消")
                .setDestructive("拍照", "从相册中选择")
                .setOthers(null)
                .setOnItemClickListener { _, position ->
                    takePhoto.onEnableCompress(CompressConfig.ofDefaultConfig(), false)
                    when (position) {
                        0 -> {
                            createTempFile()
                            takePhoto.onPickFromCapture(Uri.fromFile(mTempFile))
                        }

                        1 -> takePhoto.onPickFromGallery()
                    }
                }
                .build()
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //以下代码为处理Android6.0、7.0动态权限所需
        val type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this)
    }

    override operator fun invoke(invokeParam: InvokeParam): TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.method)
        if (TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

    /**
     * 获取TakePhoto实例
     * @return
     */
    private fun getTakePhoto() {

        takePhoto = TakePhotoInvocationHandler.of(this)
                .bind(TakePhotoImpl(this, this)) as TakePhoto

    }

    /*
      TakePhoto默认实现
   */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takePhoto.onActivityResult(requestCode, resultCode, data)
    }

    /*
      新建临时文件
   */
    private fun createTempFile() {
        val tempFileName = "${DateUtils.curTime}.png"
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            this.mTempFile = File(Environment.getExternalStorageDirectory(), tempFileName)
            return
        }

        this.mTempFile = File(filesDir, tempFileName)
    }

    /*
       获取图片，成功回调
    */
    override fun takeSuccess(result: TResult?) {
        Log.d("takePhoto", "result: ${result?.image?.originalPath}")
        Log.d("takePhoto", "result: ${result?.image?.compressPath}")
        mLocalFileUrl = result?.image?.compressPath
        mPresenter.getUploadToken()
    }

    /*
        获取图片，取消回调
     */
    override fun takeCancel() {
    }

    /*
        获取图片，失败回调
     */
    override fun takeFail(result: TResult?, msg: String?) {
        Log.e("takePhoto", msg)
    }
}
