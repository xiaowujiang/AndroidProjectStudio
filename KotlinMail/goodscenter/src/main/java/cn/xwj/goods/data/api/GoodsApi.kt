package cn.xwj.goods.data.api

import cn.xwj.baselibrary.data.protocol.BaseResp
import cn.xwj.goods.data.protocol.GetGoodsDetailReq
import cn.xwj.goods.data.protocol.GetGoodsListByKeywordReq
import cn.xwj.goods.data.protocol.GetGoodsListReq
import cn.xwj.goods.data.protocol.Goods
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Author: xw
 * Date: 2018-06-04 14:06:09
 * Description: GoodsApi: .
 */

interface GoodsApi {
    @POST("goods/getGoodsList")
    fun getGoodsList(@Body req: GetGoodsListReq): Observable<BaseResp<MutableList<Goods>?>>

    /*
       获取商品列表
    */
    @POST("goods/getGoodsListByKeyword")
    fun getGoodsListByKeyword(@Body req: GetGoodsListByKeywordReq): Observable<BaseResp<MutableList<Goods>?>>

    /*
       获取商品列表
    */
    @POST("goods/getGoodsDetail")
    fun getGoodsDetail(@Body req: GetGoodsDetailReq): Observable<BaseResp<Goods>>
}