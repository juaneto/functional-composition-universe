enablePlugins(ScalaJSPlugin)

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

name := "functionalCompositionUniverse"
scalaVersion := "2.13.6"

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.2.0"

libraryDependencies += "org.typelevel" %%% "cats-effect" % "3.2.9"

// Add support for the DOM in `run` and `test`
jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

// uTest settings
libraryDependencies += "com.lihaoyi" %%% "utest" % "0.7.9" % "test"
testFrameworks += new TestFramework("utest.runner.Framework")
