package cn.xwj.goods.ui.widget

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import cn.xwj.baselibrary.ext.loadUrl
import cn.xwj.baselibrary.widget.DefaultTextWatcher
import cn.xwj.goods.R
import cn.xwj.goods.common.GoodsConstants
import cn.xwj.goods.data.protocol.GoodsSku
import cn.xwj.goods.event.SkuChangedEvent
import cn.xwj.baselibrary.utils.YuanFenConverter
import com.eightbitlab.rxbus.Bus
import kotlinx.android.synthetic.main.layout_sku_pop.view.*
import org.jetbrains.anko.editText

/**
 * Author: xw
 * Date: 2018-06-05 16:31:36
 * Description: GoodsSkuPopView: .
 */
/*
    商品SKU弹层
 */
class GoodsSkuPopView(context: Context) : PopupWindow(context), View.OnClickListener {
    //根视图
    private val mRootView: View
    private val mContext: Context = context
    private val mSkuViewList = arrayListOf<SkuView>()

    init {
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_sku_pop, null)
        initView()

        // 设置SelectPicPopupWindow的View
        this.contentView = mRootView
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置SelectPicPopupWindow弹出窗体的高
        this.height = ViewGroup.LayoutParams.MATCH_PARENT
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.isFocusable = true
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.animationStyle = R.style.AnimBottom
        background.alpha = 150
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mRootView.setOnTouchListener { _, event ->
            val height = mRootView.findViewById<View>(R.id.mPopView).top
            val y = event.y.toInt()
            if (event.action == MotionEvent.ACTION_UP) {
                if (y < height) {
                    dismiss()
                }
            }
            true
        }


    }

    /*
        初始化视图
     */
    private fun initView() {
        mRootView.mCloseIv.setOnClickListener(this)
        mRootView.mAddCartBtn.setOnClickListener(this)

        mRootView.mSkuCountBtn.setCurrentNumber(1)
        mRootView.mSkuCountBtn.editText().addTextChangedListener(
                object : DefaultTextWatcher() {
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        Bus.send(SkuChangedEvent())
                    }
                }

        )

        mRootView.mAddCartBtn.setOnClickListener {
            dismiss()
        }
    }

    /*
        设置商品图标
     */
    fun setGoodsIcon(text: String) {
        mRootView.mGoodsIconIv.loadUrl(text)
    }

    /*
        设置商品价格
     */
    fun setGoodsPrice(text: Long) {
        mRootView.mGoodsPriceTv.text = YuanFenConverter.changeF2YWithUnit(text)
    }

    /*
        设置商品编号
     */
    fun setGoodsCode(text: String) {
        mRootView.mGoodsCodeTv.text = "商品编号:" + text
    }

    /*
        设置商品SKU
     */
    fun setSkuData(list: List<GoodsSku>) {
        for (goodSku in list) {
            val skuView = SkuView(mContext)
            skuView.setSkuData(goodSku)

            mSkuViewList.add(skuView)
            mRootView.mSkuView.addView(skuView)
        }
    }

    /*
        获取选中的SKU
     */
    fun getSelectSku(): String {
        var skuInfo = ""
        for (skuView in mSkuViewList) {
            skuInfo += skuView.getSkuInfo().split(GoodsConstants.SKU_SEPARATOR)[1] + GoodsConstants.SKU_SEPARATOR
        }
        return skuInfo.take(skuInfo.length - 1)//刪除最后一个分隔
    }

    /*
        获取商品数量
     */
    fun getSelectCount() = mRootView.mSkuCountBtn.number

    override fun onClick(v: View) {
        when (v.id) {
            R.id.mCloseIv -> dismiss()
            R.id.mAddCartBtn -> {
                dismiss()
            }
        }
    }
}
