package cn.xwj.goods.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.xwj.baselibrary.ext.loadUrl
import cn.xwj.baselibrary.ui.fragment.BaseFragment
import cn.xwj.goods.R
import cn.xwj.goods.event.GoodsDetailImageEvent
import com.eightbitlab.rxbus.Bus
import com.eightbitlab.rxbus.registerInBus
import kotlinx.android.synthetic.main.fragment_goods_detail_tab_two.*

/**
 * Author: xw
 * Date: 2018-06-05 16:30:44
 * Description: GoodsDetailTabTwoFragment: .
 */
/*
    商品详情Tab Two
 */
class GoodsDetailTabTwoFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_goods_detail_tab_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserve()
    }

    /*
        初始化监听，商品详情获取成功后，加载当前页面
     */
    private fun initObserve() {
        Bus.observe<GoodsDetailImageEvent>()
                .subscribe {
                    t: GoodsDetailImageEvent ->
                    run {
                        mGoodsDetailOneIv.loadUrl(t.imgOne)
                        mGoodsDetailTwoIv.loadUrl(t.imgTwo)
                    }
                }
                .registerInBus(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        Bus.unregister(this)
    }
}
