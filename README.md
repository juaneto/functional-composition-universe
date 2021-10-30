# Functional composition of the Universe

### Build and run
To regenerate the js file: `sbt fastOptJS`

To see the work open `functional-composition-universe-fastopt.html` file

### Config
The config is allocated in `Config` object
To activate mass of Sun slider change the `sliderActive` variable to `true`

### Functions

#### Rotate `rotate[T <: hasRotation]: T => T`
Rotation of the T spaceElement through the rotation function of the spaceElement.

#### Orbit `orbit[T <: hasOrbit, E <: SpaceElement]: (T, E) => T`
Orbit movement of the T around the E using the Physics functions of `calculateDistanceAcceleration`, `calculateAngleAcceleration` and `calculateNewOrbitPosition`.

#### LiveOnPlanet `liveOnPlanet: (Tenant, Planet) => Tenant`
Change position of the tenant inside the planet input space.

#### OrbitSatellites `orbitSatellites: Planet => Planet`
Composition of `draw`, `orbit` and `rotate` functions with the satellites of the planet as input.

#### OrbitPlanets `orbitPlanets: Star => Star` 
Composition of `draw`, `orbit`, `rotate`, `OrbitSatellites` and `tenants` functions with the planets of the star as input.

#### OrbitStars `orbitStars: BlackHole => BlackHole`
Composition of `draw`, `orbit` and `orbitPlanets` functions with the stars of the black hole as input.

#### Tenants `tenants: Planet => Planet`
Composition of `draw`, `rotate` and `liveOnMars` functions with the tenants of the planet as input.
