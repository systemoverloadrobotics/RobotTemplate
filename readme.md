# SOR 2022 Robot Code

FRC Team 6059's 2022 Robot code, built to drive an as-of-yet unnamed robot.
Our code features controls for a simple climb, intake with color detection,
swerve drive, and a shooter with Limelight target acquisition.

## Building

To build and deploy the project, simply import into "WPILib 2022 VSCode" by
opening the folder the project is checked out into, and run the action 
"WPILib: Deploy robot code." 

## Project Guidelines

Except where specified, all code in this repository should follow the Google
Java style guide, available 
[here](https://google.github.io/styleguide/javaguide.html).

Note: Google's Style guide conflicts with WPILib examples and conventions;
prefer Google's Style where a conflict exists.

Development follows the 
["Git Feature Branch" workflow](https://www.atlassian.com/git/tutorials/comparing-workflows/feature-branch-workflow), 
as described by Atlassian. Potential changes should be made on branches specific
to a given feature, and proposed by creating a pull request into `master`.

SOR2022 targets Java 11, which is required to use and deploy the robot code.
*Do not use features from standards above Java 11.*

### Branches

Pull requests into `master` should be signed off on by a mentor to ensure that
code standards are mastertained.

Once the robot is created and a program has been tested for it, we will enter
"production" mode, where a branch `live` will be created from `master`. At this
point, the robot's code will always be kept to the version at `live`, except
for testing. For a change batch to be "promoted" to `live` from `master`, it
must undergo rigerous testing. 

Discipline in ensuring the code in `live` is thoroughly tested and is promoted
in batches ensures that there will always exist a version of the robot's code
that is known good and can be used in a match.