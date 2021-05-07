# Week 8

## Samuel

I fixed and merged my PR from last week (delete an item).
The main task that I did this week was to clean up the code base (fix all lint/compiler issues, update dependencies, replace depreacted code, etc...).
On Tuesday Android Studio 4.2 was released along with new updates for dependencies/compiler and JaCoCo so I had more things to update.

I couldn't anticipate all of these releases so it took more time than expected so I didn't work on another task this week.

## Lúcás

I worked on the shake feature this week. This took up more time than expected,
because I had to learn how to use Android's sensor API, and also had to manually add
logic to convert that data into a shake event.

I'm still a bit bad at estimating tasks that aren't similar to things I've done before.

## Bastien

This week I started working on receiving push notifications, I already made a helper class to receive them from firebase and one to show them. Then I made it so a user can subscribe and unsubscribe to/from a category.
I spent quite some time reading up documentation about notifications and even more time trying to get a consistent UI when subscribing and unsubscribing.
Because of this, my time estimates were off by a factor 2 and I didn't manage to finish this task for this week.

## Benedek
This week, I merged AccountFragment into UserProfileFragment, as AccountFragment was only used for loggin in/out. We believed that it makes more sense to merge the two as the User Profile and the Account are, in a way, linked. This way, we have all we need at one place and it gets the job done faster. The main difficulty of this task was not coding or looking up documentation, but more about how to make it clean, as UserProfileFragment became quite big in terms of LoC. I therefore created an AuthHelper class that takes care of the login/logout, and did some refactoring to make the code more readable. My overall time estimate for this week was quite on point, however I spent less time than expected on testing and more time than expected on actual refactoring.

## Sebastian
I implemented a way to sort the items on the main page by different ways: category, price, date and name. I also started adding a feature on the user profile, where people can add item requests, but I’m not done yet.

The former took me longer than expected, because at first I didn’t know how to test it.

Overall, I was quite productive this week I think.

## Zacharie

This week we finished the tests with Sam. I also added a view to see a seller history and added a menu navigation to access it from the user profile frament.
My estimations of time were accurate this week.
Next week I can move on to another feature with no backlog remaining.