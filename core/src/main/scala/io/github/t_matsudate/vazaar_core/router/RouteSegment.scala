package io.github.t_matsudate.vazaar_core.router

sealed trait RouteSegment {
  def toParameterEntry(pathSegment: String): Option[(String, String)]
}

private[router] class PathSegment(val path: String) extends RouteSegment {
  override def equals(that: Any): Boolean = path.equals(that.asInstanceOf[String])

  def toParameterEntry(pathSegment: String) = None
}

private[router] class ParameterSegment(val name: String) extends RouteSegment {
  // パラメータセグメントはセグメントの文字列の内容を(少なくともこの時点では)気にしないため、比較に用いられた値に関係なく真とする。
  override def equals(that: Any): Boolean = true

  def toParameterEntry(pathSegment: String) = Some(name -> pathSegment)
}

private[router] object RouteSegment {
  def parse(segment: String): RouteSegment =
    segment
      .headOption
      .map(prefix => prefix match {
        case ':' => ParameterSegment(segment.tail)
        case _ => PathSegment(segment)
      })
      // 要求URIの先頭のスラッシュ('/')が空文字列に置き換わることを考慮します。
      .getOrElse({ PathSegment(segment) })
}
