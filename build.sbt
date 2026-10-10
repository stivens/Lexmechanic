val scala3 = "3.9.0"

ThisBuild / scalaVersion := scala3
ThisBuild / scalacOptions ++= Seq("-deprecation", "-Wunused:all")
ThisBuild / semanticdbEnabled := true

val circeVersion = "0.14.17"

lazy val noosphere = projectMatrix
  .in(file("noosphere"))
  .settings(libraryDependencies += "io.circe" %% "circe-core" % circeVersion)
  .nativePlatform(scalaVersions = Seq(scala3))
  .jsPlatform(scalaVersions = Seq(scala3))

lazy val dataSlate = project
  .in(file("data-slate"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(noosphere.js(scala3))
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "com.raquo" %% "laminar" % "17.2.1",
      "io.circe" %% "circe-parser" % circeVersion
    )
  )

val macFrameworks =
  if scala.util.Properties.isMac then Seq("-framework", "CoreGraphics", "-framework", "CoreFoundation") else Nil

lazy val linguaServitor = project
  .in(file("lingua-servitor"))
  .enablePlugins(ScalaNativePlugin)
  .dependsOn(noosphere.native(scala3))
  .settings(
    name := "lingua-servitor",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.ai" %% "claude" % "0.11.4",
      "com.softwaremill.sttp.ai" %% "openai" % "0.11.4"
    ),
    Compile / resourceGenerators += Def.task {
      val web = (Compile / resourceManaged).value / "web"
      IO.copy(
        Seq(
          (dataSlate / Compile / fullLinkJSOutput).value / "main.js" -> web / "main.js",
          (dataSlate / sourceDirectory).value / "main" / "web" / "index.html" -> web / "index.html",
          (ThisBuild / baseDirectory).value / "art" / "emblem.svg" -> web / "emblem.svg"
        )
      ).toSeq
    }.taskValue,
    nativeConfig ~= {
      _.withBaseName("lexmechanic")
        .withMultithreading(true)
        .withEmbedResources(true)
        .withResourceIncludePatterns(Seq("web/**"))
        // Scala Native's @link only takes plain libraries; the macOS hotkey needs these frameworks.
        .withLinkingOptions(_ ++ macFrameworks)
    }
  )

lazy val root = project
  .in(file("."))
  .aggregate(linguaServitor, dataSlate)
  .aggregate(noosphere.projectRefs*)
  .settings(
    name := "lexmechanic",
    publish / skip := true
  )

addCommandAlias("release", "linguaServitor/nativeLinkReleaseFast")
