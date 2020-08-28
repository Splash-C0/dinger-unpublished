# Dinger [![CircleCI](https://circleci.com/gh/stoyicker/dinger.svg?style=svg&circle-token=d5cbf3eff73b1d9431d9a69adc1cacc48c4feac4)](https://circleci.com/gh/stoyicker/dinger)

## What Tinder should have been

Tinder is a very trendy thing. It's like clubbing, but without the fun :D

From a technical standpoint however, the Android app has some... 'shortcomings':
* It requires you to periodically perform a monkey task (swiping), which is cumbersome and inefficient, and could be easily automated.
* It performs a very aggressive polling to check for changes that may affect your experience (in other words, it continuously asks the server 'did anyone message me?' instead of letting the server notify the device when a message came). This results in unnecessary battery and data consumption plus, when implemented poorly, buggy behavior too.
* It does not support landscape mode.
* It requires Internet access to even enter the app.
* It does not support multi-window mode.

This app exists to show that these and other pain points are easily addressed nowadays if things are done correctly. And for my and every other Tinder user's joy too, of course :)

## Usage instructions

Once you have the app installed, and you have an account registered in Tinder using Facebook (that is, you log in onto Tinder using your Facebook account), just open Dinger and log in with your Facebook account so it can have your Tinder credentials. After that, it will work on its own.

## Data tracking

This application uses Bugsnag to track some crash data. If this seems to invasive for you, the application also includes a "Void" crash reporter, that does nothing when otherwise a crash would be reported. In order to enable it, look for CrashReporterModule classes and make them provide a CrashReporters.void() instance.

## Distribution

The first time, you need to install the apk manually. In addition, whenever you open the app, you will get notified if newer versions are available and can download them via the [website](https://stoyicker.github.io/dinger/#download "Dinger APK download").

## Feature requests & bug reports

Something not working as expected? Missing something you'd like to be able to do? [Open an issue!](https://github.com/stoyicker/dinger/issues/new "New issue - stoyicker/dinger")

## API overview

[Click here instead](https://app.swaggerhub.com/apis/stoyicker/app.tinder-dinger/ "Tinger by Dinger (unofficial) on SwaggerHub"). Source [here](https://github.com/stoyicker/dinger-swagger "stoyicker/dinger-swagger")

## Building

See [BUILDING.md](BUILDING.md "BUILDING.md").

## Contributing 

See [CONTRIBUTING.md](CONTRIBUTING.md "CONTRIBUTING.md").
