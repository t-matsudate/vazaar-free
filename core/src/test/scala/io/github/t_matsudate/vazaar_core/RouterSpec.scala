import org.scalatest.flatspec.AnyFlatSpec

import io.github.t_matsudate.vazaar_core.Handler
import io.github.t_matsudate.vazaar_core.router.*

class RouterSpec extends AnyFlatSpec {
  behavior of "パスセグメントのみのルート:"

  val nothingHandler = new Handler {}

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
}
