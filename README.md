# Double Pendulum 3D

---

A 3D visualisation of a 3D pendulum

Made By Hans Zuidervaart.

---

## Screenshots

#### screenshot 1: A preview of the application

![Pendulum](src/main/resources/images/example1.jpg)

#### screenshot 2:

![Pendulum](src/main/resources/images/example2.jpg)

#### screenshot 3:

![Pendulum](src/main/resources/images/example3.jpg)

---

## Motivation

I was inspired by [this](https://www.youtube.com/watch?v=uWzPe_S-RVE) the coding train episode!

---

## Background

---

The motion of a double pendulum can be described by a system of ordinary differential equations (ODE's).

For background on how these equations can be derived and implemented in code, look at the following
paper: [Thesis Kessels][Thesis_BTW_Kessels]

---

## Setup

The project is set up in such a way that it should feel like a plug and play experience.

Use `mvn javafx:run` to run the application.

---

## Tests

Some tests make use of TestFx

To let TestFX manage Mouse and Keyboard on macOS, you have to authorized it.

* Go to System Settings > Privacy & Security > Accessibility
    * If you want to execute the tests from your IDE, check your IDE in the list
    * If you want to execute the tests from maven/gradle in your terminal, check the Terminal
    * If you don't find the Terminal or your IDE, click on + and add it to the list
        * Terminal should be there /Applications/Utilities/Terminal.app

See also [Fix with documentation](https://github.com/bjalon/TestFX/commit/13cdd07e5e6091a5f067edc1a80b8b126de3fd0b)

---

## Sources

- [openjfx-docs IDE Intellij](https://openjfx.io/openjfx-docs/#IDE-Intellij)
- [ArchUnit User Guide](https://www.archunit.org/userguide/html/000_Index.html#_introduction)

[Thesis_BTW_Kessels]:docs/Thesis_BTW_Kessels.pdf

---
