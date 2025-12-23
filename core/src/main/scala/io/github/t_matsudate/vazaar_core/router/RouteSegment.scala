package io.github.t_matsudate.vazaar_core.router

sealed trait RouteSegment

private[router] class PathSegment(val path: String) extends RouteSegment {
  override def equals(that: Any): Boolean = path.equals(that.asInstanceOf[String])
}

private[router] object RouteSegment {
  def parse(segment: String): RouteSegment =
    segment
      .headOption
      .map(prefix => prefix match {
        case _ => PathSegment(segment)
      })
      // 要求URIの先頭のスラッシュ('/')が空文字列に置き換わることを考慮します。
      .getOrElse({ PathSegment(segment) })
}
