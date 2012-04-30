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
- SBT 0.11.2 (https://github.com/harrah/xsbt/wiki for installation, getting started, etc)
- An idea that you feel comfortable with (this guide will use IntelliJ IDEA but you might as well use the Scala IDE in Eclipse)

Installation Instructions
-------------------------

1. Open a terminal window and clone the project to your disk: ``>git clone git@github.com:henrikengstrom/roygbiv.git``

2. Start SBT: ``$ sbt``

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

Running the system
------------------

To run the system all you have to do it open three terminal windows/tabs.
But before that you should make sure that the mandatory Roygbiv jars are available in your Ivy repository.
This is done with some help from SBT::
  $ cd PROJECT_HOME
  $ sbt
  > publish-local

Now you are ready to start rendering.
To start the rendering server::
  $ cd PROJECT_HOME
  $ sbt
  > project server
  > run

To start a client::
  $ cd PROJECT_HOME
  $ sbt
  > project client
  > run

To start the web interface (and thereby also the rendering process)::
  $ cd PROJECT_HOME/web
  $ sbt
  > run

Now you open a browser and go to: http://localhost:9000
Click on the ``Start`` button to start the rendering. After a while you should see the result being pushed to your browser.

NOTE: The whole start/stop process is a bit brittle at the moment (to say the least).
Should something go wrong you have to restart them in the above stated order.
This is something that will be improved asap.

NOTE2: The system is optimized to run the client(s) on a separate machine (since the client uses pinned dispatchers
corresponding to the number of cores in the CPU). Should you run the whole setup on the same machine you may want to
set the number of processors to less than your actual number to avoid unnecessary context switching, see
roygbiv.client.Worker)

Disclaimer
----------

This is a hobby hack and the quality might be thereafter!
Feel free to use, copy, alter, play with, disregard, tweet about, drink beer (responsibly) and whatever you feel like.
It's a free world out there, you might as well enjoy it.
