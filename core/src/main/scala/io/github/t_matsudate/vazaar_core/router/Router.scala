package io.github.t_matsudate.vazaar_core.router

import scala.collection.mutable.{
  ArrayBuffer,
  HashMap,
  LongMap,
}

/** # 長さ優先の要求ルータ
  *
  * 要求URIとそれに対応するハンドラの組を管理します。
  * その際、それらの組を以下のように変換します。
  *
  * `(要求URI, ハンドラ) => (セグメント数, ルート)`
  *
  * ここで、上記の各用語はそれぞれ以下の意味を持ちます。
  *
  * |用語|意味|
  * | :- | :- |
  * |要求URI|要求に含まれる、アクセス対象のリソースを指すURI。|
  * |ハンドラ|アクセス対象のリソースを返送する処理が記述された、何らかのインスタンス。|
  * |セグメント数|要求URIをパスの区切り文字(`/`)を基準に分割して得られる、配列の長さ。|
  * |ルート|セグメントの配列と要求URIに対応するハンドラの組から作られる、ルータの構成要素。|
  *
  * ## 要求URI
  * 
  * HTTP(RFC 9110)の仕様上、要求URIとして想定される形式は以下の4つです。
  *
  * |形式|概要|例|
  * | :- | :- | :- |
  * |オリジン形式|リソース返送要求|`/path/to/target`|
  * |絶対URI形式 / 相対 URI形式|プロキシ接続要求|`https://example.com/path/to/target` / `//example.com/path/to/target`|
  * |権限形式|トンネル確立要求|`example.com:443`|
  * |アスタリスク形式|接続方法説明要求|`*`|
  *
  * ## セグメント
  *
  * 要求URIから作られるセグメントの配列は、以下の3つのパターンに分かれます。
  *
  * |セグメント数|要求URIの形式|パターン|
  * | -: | :- | :- |
  * |0|オリジン形式|ドキュメントルート(`/`)|
  * |1|<ul><li>権限形式</li><li>アスタリスク形式</li></ul>||
  * |2以上|<ul><li>オリジン形式</li><li>絶対URI形式 / 相対 URI形式</li></ul>|<ul><li>ドキュメントルート以外</li><li>プロトコルスキームが加味された、セグメントの配列(コロン(`:`)を含む)</li></ul>|
  */
class Router private (private val routeMap: LongMap[ArrayBuffer[Route]]) {
  /** 各ルートの中から要求URIに対応するハンドラを探し出します。
    *
    * ハンドラは以下の順序で探し出されます。
    *
    * 1. 要求URIをセグメントに分割します。
    * 2. 各セグメントのパターンと完全一致するルートを探し出します。
    * 3. 見つかったルートのインスタンスが持つハンドラを返します。
    *
    * @param requestURI 要求URIです。
    * @return いずれかのルートに含まれる要求URIに対応するハンドラと、そのルートの中でパラメータ化されたセグメントを返します。
    *         対応するハンドラが見つからない場合にのみ、Noneを返します。
    *         セグメント数が一致しない場合も、対応するハンドラが見つからないと見なします。
    */
  def findHandler(requestURI: String): Option[(Handler, HashMap[String, String])] = {
    val segments = requestURI.split("/+")
    val segmentLength = segments.length

    routeMap
      .get(segmentLength)
      .flatMap(_.find(_.isMatched(segments)))
      .map(_.associateParameters(segments))
  }
}

object Router {
  /** 要求URIとそれに対応するハンドラの組で新しい要求ルータを構築します。
    *
    * 要求URIのセグメントとして、以下のものがあります。
    *
    * * ':' で始まるセグメント: ルートのパラメータとして扱うセグメント
    * * 他: 単純なパスのセグメント
    *
    * @param elems 要求URIとそれに対応するハンドラの組です(可変長)。
    * @return `elems` に含まれる組を元に作られた、新しい要求ルータのインスタンスです。
    */
  def apply(elems: (String, Handler)*): Router = {
    val routeMap: LongMap[ArrayBuffer[Route]] = LongMap()

    elems.foreach(elem => {
      val segments = elem
        ._1
        .split("/+")
      val segmentLength = segments.length
      val route = Route(segments, elem._2)

      routeMap.get(segmentLength) match {
        case Some(routes) => routes.addOne(route)
        case None => {
          val routes: ArrayBuffer[Route] = ArrayBuffer()
          routes.addOne(route)
          routeMap.update(segmentLength, routes)
        }
      }
    })

    new Router(routeMap)
  }
}
