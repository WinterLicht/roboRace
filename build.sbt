name := "roboRace"

scalaVersion in ThisBuild := "2.12.7"

lazy val gameLogic = project.in(file("gameLogic"))
  .settings(folderSettings, monocle, scalaTest)


lazy val ai = project.in(file("ai"))
  .dependsOn(gameLogic)
  .settings(folderSettings, breeze, scalaTest)


lazy val service = project.in(file("service"))
  .dependsOn(gameLogic)
  .settings(scalaTest)
  .enablePlugins(PlayScala)
  .settings(playDependencies)
  .enablePlugins(EmbeddedPostgresPlugin)
  .settings(
    javaOptions += s"-DDATABASE_URL=${postgresConnectionString.value}",
    postgresSilencer := true
  )

def folderSettings = Seq(
  scalaSource in Compile := baseDirectory.value / "src",
  scalaSource in Test := baseDirectory.value / "test"
)

def scalaTest = libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test

def monocle = Seq(
  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %% "monocle-core" % "1.5.0",
    "com.github.julien-truffaut" %% "monocle-macro" % "1.5.0",
    "com.github.julien-truffaut" %% "monocle-unsafe" % "1.5.0"
  ),
  addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.1" cross CrossVersion.full)
)

def playDependencies = Seq(
  libraryDependencies += guice,
  libraryDependencies += "com.dripower" %% "play-circe" % "2609.1",
  libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5",
  libraryDependencies += evolutions,
  libraryDependencies += jdbc,
  libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.2",
  libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)

def breeze = Seq(
  libraryDependencies += "org.scalanlp" %% "breeze" % "0.13.2",
  resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)