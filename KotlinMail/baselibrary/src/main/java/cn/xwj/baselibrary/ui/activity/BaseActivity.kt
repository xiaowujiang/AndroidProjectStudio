package cn.xwj.baselibrary.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import cn.xwj.baselibrary.utils.AppManager

/**
 * Author: xw
 * Date: 2018-05-25 15:03:06
 * Description: BaseActivity: .
 */
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.instances.addActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.instances.removeActivity(this)
    }


    val contentView: View
        get() {
            val parent = this.window.decorView as ViewGroup
            return parent.getChildAt(0)
        }
}