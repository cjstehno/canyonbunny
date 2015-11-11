# Canyon Bunny

This is the tutorial game from the book 
"[Learning LibGDX Game Development](https://www.packtpub.com/game-development/learning-libgdx-game-development-second-edition)" - translated to Groovy, of course.

> Warning: this code is inspired by the book; however, I have made quite a few changes (for the better, I feel).

## Build

    ./gradlew clean build pack dist
    
(you can omit `pack` and `dist` if you are just doing a development build.)

## Run

For development:

    ./gradle run 

For distribution:

    java -jar desktop-1.0.jar

(just make sure you have run the `dist` task to generate the jar file).
