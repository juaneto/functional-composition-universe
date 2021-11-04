# Functional composition of the Universe

### Slides ScalaCon 2021
https://docs.google.com/presentation/d/14d3IJNcqBcRHS2IHwOPus7ygFB8BZNzRbwj-0W66xDo

### Build and run
To regenerate the js file: `sbt fastOptJS`

To see the work open `functional-composition-universe-fastopt.html` file

### Config
The config is allocated in `Config` object
To activate mass of Sun slider change the `sliderActive` variable to `true`

### Functions

#### Rotate 
Rotation of the T spaceElement through the rotation function of the spaceElement.

#### Orbit 
Orbit movement of the T around the E using the Physics functions of `calculateDistanceAcceleration`, `calculateAngleAcceleration` and `calculateNewOrbitPosition`.

#### LiveOnPlanet
Change position of the tenant inside the planet input space.

#### OrbitSatellites
Composition of `draw`, `orbit` and `rotate` functions with the satellites of the planet as input.

#### OrbitPlanets
Composition of `draw`, `orbit`, `rotate`, `OrbitSatellites` and `tenants` functions with the planets of the star as input.

#### OrbitStars
Composition of `draw`, `orbit` and `orbitPlanets` functions with the stars of the black hole as input.

#### Tenants
Composition of `draw`, `rotate` and `liveOnPlanet` functions with the tenants of the planet as input.
