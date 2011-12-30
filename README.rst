ROYGBIV
=======

The aim of this project is to build a ray tracer in Scala backed by the Akka framework.
It is solely a hobby project and the progress might be slow depending on the real work load of the authors.
It's being developed by two Swedish geeks from which one is reachable on Twitter with the tag ``@h3nk3``
Please feel free to contact us for any inquiries.

The name of the system is two folded in that it, of course, represents the colors of the rainbow and colors is what
ray tracing is all about, but also that Roygbiv happens to be the name of a fantastic song by the eminent group
`Boards of Canada <http://www.youtube.com/watch?v=yT0gRc2c2wQ>`_

Requirements
------------

You should install the following software to get started:

- GIT
- SBT 0.11.2 (see https://github.com/harrah/xsbt/wiki for installation, getting started, etc)
- An idea that you feel comfortable with (this guide will use IntelliJ IDEA but you might as well use the Scala IDE in Eclipse)

Installation Instructions
-------------------------

1. Open a terminal window and clone the project to your disk: ``>git clone git@github.com:henrikengstrom/roygbiv.git``

2. Start SBT: ``> sbt``

After some intial logging you should see something like this::

  [info] Set current project to default-77212f (in build file:/code/roygbiv/)
  >

This means you are good to go!
Test to compile the system with::

  > compile

3. Generate IntelliJ IDEA files from SBT. While in SBT enter the following: ``> gen-idea``

For more information about generating IDEA files see `here <https://github.com/mpeltonen/sbt-idea>`_

There might be a couple of warnings but you can disregard these.
Wait until you can see the SBT prompt again.
You should now have a ``.idea`` and a ``.idea_modules`` file in your project.
Open IDEA and select to open a project, select the "roygbiv" folder where you have the project and that should be it.

Before you can handle Scala in IDEA you must download the Scala plugin (IntelliJ IDEA -> Preferences... -> Plugins -> Scala)
While you're at it you might as well download the SBT plugin so that you can run SBT from inside the best IDE out there :-)

After importing the project you may want to "clean up" the project settings.

- Inside the IDEA right-click the "roygbiv" and select Open Module Settings.
- In the sources tab the following source folders should be available:
  src/main/scala
  src/main/config
- In the sources tab the following test folders should be available:
  src/test/scala

*Note:*
Please make sure that the IDEA related files are not pushed into the repository as they are client specific!
Maybe it goes without writing but if the SBT project is updated, e.g. with new dependencies, you must re-generate your IDEA files again. 

Running the system
------------------

First you should start the server. This is the hub of the calculation in that it will store all calculations and
save them onto disk. Therefore it needs to be started before we perform any calculations.

1. Run -> Edit Configurations
2. Click the + sign and add Application
3. Enter the following information:
   - Name: "server"
   - Main class: akka.kernel.Main
   - VM parameters: -Xms512M -Xmx1024M
   - Program Arguments: roygbiv.server.Server
   - Working directory: /Users/he/code/roygbiv
   - Use classpath of module: server
   - Before launch: Select Make and "compile" as target
4. Click OK

Start the server by clicking the run arrow and select ``server``.

Next it is time to start a client.

1. Run -> Edit Configurations
2. Click the + sign and add Application
3. Enter the following information:
   - Name: "client"
   - Main class: akka.kernel.Main
   - VM parameters: -Xms512M -Xmx1024M
   - Program Arguments: roygbiv.client.Client
   - Working directory: /Users/he/code/roygbiv
   - Use classpath of module: client
   - Before launch: Select Make and "compile" as target
4. Click OK

Start the client by clicking the run arrow and select ``client``.

This should start the ray tracing. Open up an activity monitor on your machine(s) and see how the CPU is glowing red hot.
The result will be a file called ``result.png`` which you can find in the root of the project.
This file is improved every 3 seconds so keep the ray tracing running for a while and you will see the improvement of
the image as more results are returned from the client(s).
To stop the rendering just stop the client(s) from running.

Future Improvements and Additions
---------------------------------

* Play 2.0 interface with which it is possible to start, stop and see the result of the rendering.
* Easy setup of adding and removing clients.
* More cool stuff.

Disclaimer
----------

This is a hobby hack and the quality might be thereafter!
Feel free to use, copy, alter, play with, disregard, tweet about, drink beer (responsibly) and whatever you feel like.
It's a free world out there, you might as well enjoy it.
