# Week 2

## Ben
A feature that has been added this week is putting the price into the New and Edit item view. Therefore, when a user wants to create a new item or edit an already existing one, they can now also set the price and edit it, not only the title and the description. When the field for the price is left blank, a default price of 0.0 is set for that particular item.

Overall this task was not too difficult and did not take a huge amount of time, however, issues have occurred with Cirrus which did not help us merge the pull request as fast as planned.

## Lúcás
First I implemented extra forms for adding both a title and a description when creating and editing items.
This took the 2 hours I expected, given that I had familiarize myself with the fragments the others had made.

Then, I added an extra view for seeing all of the components of an item. This took about as long as I expected,
since I needed to figure out how to implement click interactions for list items, and also modify
the navigation graph.

I started looking into implementing caching, but researching how room works, and different caching
strategies took longer than expected.

## Sebastian
I implemented the display of the map, with the current location of the user being refreshed every 2.5-5 seconds, automatically. This took me 7 hours, which is slightly lower than the 8 hours I estimated for it. 

Overall, I am feeling much more confident using git, with merges and pull requests. I am also getting more familiar with Kotlin, and I struggle less than before with Android Studio.

## Samuel
Last week we didn't have time to perfect the integration of the database and the other features (add and edit items, navigation).
So I've checked with Bastien who did the item edit and fragment navigation to improve the integration.

Then I added new classes to store and retrieve users and did some refactoring.
It will be useful when we'll do the user authentication next week with Ben.

I also continued a bit with the Android Kotlin codelabs and helps my teammates who were using the database.

My time estimate was more accurate this time.

## Zach
This week I finished the kotlin course from the previous sprint, I also implemented a category attribute for items and modified the existing code to work with it.

I had estimated only an hour for this task but in the end it took over 6h to complete. Merging with the main branch also proved quite difficult as my branch was many commits behind.

I was unable to finish the implementation of the search functionality due to time constraints so I intend to finish it during the next sprint.

## Bastien
I checked with Samuel the integration concerning the database and the other features.
Then I started researching how to add images from the gallery in the app and started implementing it.

The feature is not finished because I needed to refactor the code, but I can't find a way to make it work while being separated despite receiving help from Lucas.

My time estimate was again far from reality.

## Overall team
We only had one stand-up meeting at the beginning of the week but we stayed in contact on the WhatsApp group.

The pull requests where spread throughout the week so it was easier to integrate them and we were not blocking each others.
Also everyone contributed for the code reviews and had at least a feature merged.

We still had some issues with Cirrus CI who went down on Monday and made us lose some time.
But the rest of the week we had less issues with it. 

A large part of features were implemented but we still have a few left for next week (authentication, search and cache). 
