package io.github.t_matsudate.vazaar_core.router

import scala.collection.mutable.HashMap

import io.github.t_matsudate.vazaar_core.Handler

private[router] class Route private (
  private val routeSegments: Array[RouteSegment],
  private val handler: Handler,
) {
  def isMatched(pathSegments: Array[String]): Boolean =
    routeSegments
      .zip(pathSegments)
      .forall(segmentPair => segmentPair._1.equals(segmentPair._2))

  def associateParameters(pathSegments: Array[String]): (Handler, HashMap[String, String]) = {
    val parameterEntries = routeSegments
      .zip(pathSegments)
      .map(segmentPair => segmentPair._1.toParameterEntry(segmentPair._2))
      .filter(_.isDefined)
      .map(_.get)

    handler -> HashMap.from(parameterEntries.iterator)
  }
}

private[router] object Route {
  def apply(pathSegments: Array[String], handler: Handler): Route = new Route(
    pathSegments.map(RouteSegment.parse),
    handler
  )
}
