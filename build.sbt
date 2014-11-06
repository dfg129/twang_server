import scalariform.formatter.preferences._

name := "twang-server"

version := ".01"

scalaVersion := "2.11.2"

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-stream-experimental" % "0.9"
	)
	
scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
	.setPreference(AlignSingleLineCaseStatements, true)
	.setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
	.setPreference(DoubleIndentClassDeclaration, true)
	.setPreference(PreserveDanglingCloseParenthesis, true)