import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.mutable.HashMap

import io.github.t_matsudate.vazaar_core.Handler
import io.github.t_matsudate.vazaar_core.router.*

class RouterSpec extends AnyFlatSpec {
  val nothingHandler = new Handler {}

  behavior of "パスセグメントのみのルート:"

  it must "リクエストURIに対応するハンドラが見つからない場合はNoneを返す。" in {
    val mustBeNone = Router(
      "/" -> nothingHandler,
    )
    assert(mustBeNone.findHandler("/must-be-none").isEmpty)
  }

  it must "リクエストURIに対応するハンドラが見つかる場合はSome(handler)を返す。" in {
    val mustBeSome = Router(
      "/must-be-some" -> nothingHandler,
    )
    assert(mustBeSome.findHandler("/must-be-some").isDefined)
  }

  behavior of "パラメータセグメントを含むルート:"

  it must "対応するセグメントがルートのパラメータになる。" in {
    val mustHaveParameters = Router(
      "/:user" -> nothingHandler
    )
    val result = mustHaveParameters.findHandler("/me")
    assert(result.isDefined)
    assertResult(result.get._2)(HashMap("user" -> "me"))
  }

  it should "前後に他の種類のセグメントがあっても、パラメータ化に成功する。" in {
    val shouldSucceedWithOtherSegments = Router(
      "/:user/about" -> nothingHandler,
      "/about/:user" -> nothingHandler
    )
    val resultOfBeforePathSegment = shouldSucceedWithOtherSegments.findHandler("/me/about")
    assert(resultOfBeforePathSegment.isDefined)
    assertResult(resultOfBeforePathSegment.get._2)(HashMap("user" -> "me"))
    val resultOfAfterPathSegment = shouldSucceedWithOtherSegments.findHandler("/about/me")
    assert(resultOfAfterPathSegment.isDefined)
    assertResult(resultOfAfterPathSegment.get._2)(HashMap("user" -> "me"))
  }
}
