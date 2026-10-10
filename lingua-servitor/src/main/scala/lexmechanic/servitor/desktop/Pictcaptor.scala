package lexmechanic.servitor.desktop

import java.io.File
import java.nio.file.{Files, Path, Paths}
import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.*
import scala.scalanative.meta.LinktimeInfo

object Pictcaptor:
  // A capture tool can hang on a permission prompt; without a limit the servitor would stay busy until restarted.
  private val TimeoutSeconds = 20L

  private val linuxCommands = List(
    List("spectacle", "--background", "--nonotify", "--fullscreen", "--output", "{file}"),
    List("gnome-screenshot", "--file={file}"),
    List("grim", "{file}"),
    List("scrot", "{file}"),
    List("import", "-window", "root", "{file}")
  )

  lazy val defaultCommand: List[String] =
    if LinktimeInfo.isMac then List("screencapture", "-x", "-t", "png", "{file}")
    else linuxCommands.find(command => installed(command.head)).getOrElse(linuxCommands.head)

  private def installed(program: String): Boolean =
    sys.env
      .get("PATH")
      .toList
      .flatMap(_.split(File.pathSeparator))
      .exists(dir => Files.isExecutable(Paths.get(dir, program)))

  // Translation and calibration can both capture at once; screenshot tools are not built to run concurrently.
  def fullScreen(command: List[String], target: Path): Path = synchronized {
    Files.createDirectories(target.getParent)
    Files.deleteIfExists(target)
    val output = target.resolveSibling(s"${target.getFileName}.log")
    val process = ProcessBuilder(command.map(_.replace("{file}", target.toString)).asJava)
      .redirectErrorStream(true)
      .redirectOutput(output.toFile)
      .start()
    val commandLine = command.mkString(" ")
    if !process.waitFor(TimeoutSeconds, TimeUnit.SECONDS) then
      process.destroyForcibly()
      throw RuntimeException(s"Capture command timed out after ${TimeoutSeconds}s: $commandLine")
    val exit = process.exitValue()
    if exit != 0 || !Files.exists(target) then
      throw RuntimeException(s"Capture command failed (exit $exit): $commandLine\n${Files.readString(output).trim}")
    target
  }
