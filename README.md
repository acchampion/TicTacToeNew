Tic-Tac-Toe for Android
=======================

Play Tic-Tac-Toe on your Android smartphone! This app also demonstrates how to use various Android
programming techniques. It's the main reference for the course:
[CSE 5236: Mobile Application Development](http://web.cse.ohio-state.edu/~champion/5236) at
[The Ohio State University](https://www.osu.edu).
(**Note:** Course materials are available to students via [Carmen-Canvas](https://carmen.osu.edu).)

**Caveats:**
- The maps part of the app won't work unless you get your own [Mapbox](https://www.mapbox.com) token. You'll need to set a key-value pair, `MAPBOX_ACCESS_TOKEN`, to your token in your own `gradle.properties` file. [Mapbox's instructions](https://docs.mapbox.com/help/troubleshooting/private-access-token-android-and-ios/#android)]
- Similarly, the Flickr Photo Gallery won't work unless you obtain a [Flickr](https://www.flickr.com) API token. Like before, you'll need to set a key-value pair, `FLICKR_ACCESS_TOKEN`, to your token in this file.
- Mapbox's new [map search SDK](https://docs.mapbox.com/android/search/examples/autofill-ui/) uses [Kotlin coroutines](https://kotlinlang.org/), which are unavailable in Java. To simplify the code, I omitted this feature, and I'll place it in the [Kotlin version of Tic-Tac-Toe](https://github.com/acchampion/TicTacToeKotlin). (Esoco GmbH has an [implementation of coroutines in Java](https://github.com/esoco/coroutines); look at [their Medium article](https://www.google.com/url?sa=t&source=web&cd=&cad=rja&uact=8&ved=2ahUKEwiHgfPu_Pf_AhX9FVkFHRJeBgQQFnoECAQQAQ&url=https%3A%2F%2Fmedium.com%2F%40esocogmbh%2Fcoroutines-in-pure-java-65661a379c85&usg=AOvVaw0t9fbvBIRfL3H-P-zrMDPe&opi=89978449) and [a StackOverflow post](https://stackoverflow.com/questions/2846664/implementing-coroutines-in-java) for more information.)

License
=======

[Apache 2.0](https://apache.org/licenses/LICENSE-2.0)

Contributions
=============
- Original code by [Prof. Rajiv Ramnath](http://web.cse.ohio-state.edu/~ramnath). I refactored the code to use Fragments and fetch photos from [Flickr](https://www.flicr.com).
- [Android Programming: The Big Nerd Ranch Guide](https://www.bignerdranch.com/books/android-programming/) for SingleFragmentActivity and the [Flickr](https://www.flickr.com) photo-fetching code
- Mapbox's [code to find the user's location](https://docs.mapbox.com/android/maps/examples/location-tracking/)

Assets
======
- [Sintel](http://www.sintel.org) trailer, [Blender Foundation](http://www.blender.org), 2011, CC-BY-3.0
- John Philip Sousa, "The Stars and Stripes Forever," Edison Recordings, public domain

Further Resources
=================
-
