# Week 1

## Bastien

I followed the [android kotlin codelabs](https://developer.android.com/courses/kotlin-android-fundamentals/overview) to to understand how to have a good architecture.
I then converted the existing Activities in Fragments and added navigation while keeping the same functionality.
Finally, by following the codelab concerning RecyclerViews, I added a button next to an item to be able to edit it.
I did not have enough time to start on researching how to add images in the application from the phone nor to implement it.

My time estimate was way off because I had to look up a lot of resources to understand the existing code and try to implement my functionalites nicely.

Next time, I'll know where some specific resources are so that I won't spend as much time looking for it.

## Ben

Another feature added this week was setting the price of an item in a separate view. By clicking on "Set Price" from the Main Activity, we are prompted to another activity where we can type in the price. If nothing is typed then the default price set will be 0.0. After clicking on OK when having typed on the price, a new activity prompts (a summary of setting the price) that shows the price we typed in previously.

## Lúcás

First I implemented a simple form for creating new items. This took 3 hours instead of the
2 hours I estimated, because I was a bit rustier on Android than I thought, so figuring
out how to do the basics took a bit longer.

Then I worked on creating a list for viewing multiple items. This took me 3 hours instead
of 2, mainly because figuring out how to use a RecyclerView was more complicated
than I anticpated.

I had started working on adding persistence, and basically finished, but then Samuel's
work on Firebase basically upended my work, at least for now.

## Sebastian

I first needed a refresh on android and Kotlin, so I read a basics course.
This took me 5 hours, which is what I estimated.

Then I implemented a way to fetch the location of the device, which required a way to ask for permissions.
I miscalculated the time it took me to implement this feature.
Instead of 3 hours, it took my around 8 hours, because a lot of documentation was either deprecated or not complete.

Then I had some troubles with the pull requests and merging, as I had never done that before.

## Samuel

I did some research about Firebase, and documented my findings.
Then I implemented a repository to store and retrieve items using Firestore.

My time estimate was accurate for the research but for the implementation it took more time.

For this first sprint, we were a bit late to merge all the changes. I had to wait for other tasks before merging but luckily the architecture we had was good and I didn't have to change a lot of code to merge.

## Zach

I have read and done the vast majority of the Android Kotlin course provided by Goole. I have also read the report about firebase made by Samuel.

I started making a view for the search page of the application however I have to turn it into a fragment for which I need to finish the Kotlin course.

My time estimation and management was very much wrong and it is taking longer than expected.

Next time, I'd like to finish the view for the search page as well as it's actual functionality.

## Scrum Master (Lúcás)

One issue at the start of the week was that too little work was assigned. Thankfully
we had a special meeting on Monday to resolve this.

Another issue was that the work wasn't spread out well throughout the week, which led
to everybody making pull requests on Thursday Night. This lead to a lot of complicated
merges, because as each pull request was merged changing a lot of how the app worked,
everybody else had to rebase their changes. This is tricky to do correctly
the first time, and our inexperience with integrating changes lead to many a mistake
and frustration.

A lot of estimates were too low, probably because of our inexperience with Android.
I also think that a lot of tickets ended up exceeding their scope. For example,
one ticket involved editing items, but ended up being extended into adding navigation. This
was necessary, but keeping smaller tickets would've let us extend the work more smoothly
throughout the week, and avoid the aforementioned pile-ups.

On the bright side, our standup meetings did go well, although we should be a bit
more forward next time to try and avoid all merging a bunch of work at the same time.
