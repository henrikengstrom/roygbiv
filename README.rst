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

Running the system
------------------

TODO : Write new instructions

Disclaimer
----------

This is a hobby hack and the quality might be thereafter!
Feel free to use, copy, alter, play with, disregard, tweet about, drink beer (responsibly) and whatever you feel like.
It's a free world out there, you might as well enjoy it.
