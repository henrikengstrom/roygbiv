resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

resolvers += "Atmos Repo" at "http://repo.typesafe.com/typesafe/atmos-releases/"

credentials += Credentials(Path.userHome / "atmos.credentials")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

addSbtPlugin("com.typesafe.atmos" %% "atmos-deploy-local" % "1.2")



