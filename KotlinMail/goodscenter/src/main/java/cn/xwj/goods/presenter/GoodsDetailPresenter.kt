package cn.xwj.goods.presenter

import cn.xwj.baselibrary.ext.execute
import cn.xwj.baselibrary.presenter.BasePresenter
import cn.xwj.baselibrary.rx.BaseSubscriber
import cn.xwj.baselibrary.utils.AppPreferences
import cn.xwj.goods.common.GoodsConstants
import cn.xwj.goods.data.protocol.Goods
import cn.xwj.goods.data.repository.CartDataSource
import cn.xwj.goods.data.repository.GoodsDataSource
import cn.xwj.goods.presenter.view.GoodsDetailView
import javax.inject.Inject

/**
 * Author: xw
 * Email:i.xiaowujiang@gmail.com
 * Date: 2018-06-03 2018/6/3
 * Description: CategoryPresenter
 */
class GoodsDetailPresenter @Inject constructor() : BasePresenter<GoodsDetailView>() {


    @Inject
    lateinit var repository: GoodsDataSource
    @Inject
    lateinit var cartRepository: CartDataSource

    fun getGoodsDetail(goodsId: Int) {

        repository.getGoodsDetail(goodsId)
                .execute(object : BaseSubscriber<Goods>(mView) {
                    override fun onComplete() {
                        super.onComplete()
                        mView.onGetGoodsDetailResult(mData!!)
                    }
                }, lifecycleOwner)
    }

    /*
        加入购物车
     */
    fun addCart(goodsId: Int, goodsDesc: String, goodsIcon: String, goodsPrice: Long,
                goodsCount: Int, goodsSku: String) {
        mView.showLoading()
        cartRepository.addCart(goodsId, goodsDesc, goodsIcon, goodsPrice,
                goodsCount, goodsSku).execute(object : BaseSubscriber<Int>(mView) {
            override fun onComplete() {
                super.onComplete()
                AppPreferences.instance.put(GoodsConstants.SP_CART_SIZE, mData!!)
                mView.onAddCartResult(mData!!)
            }
        }, lifecycleOwner)
    }
}