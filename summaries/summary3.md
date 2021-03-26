# Week 3

## Bastien
First I finished adding the possibility to add images from the gallery in the new item and edit item.
Then I started working on adding the possibility to do so with the camera but didn't manage to finish it.

The feature is not finished because I needed to refactor the code, but I can't find a way to make it work while being separated despite receiving help from Lucas.

My time estimate was a bit off for the first task, and a lot off for the second one.

## Benedek
This week, we implemented the login via Google. A user can now use their Google account to log in. They press the "Account" button on the main screen and will get directed to their "account" page where they can use the Login button (that has a Google logo) in order to log in. They will get prompted to an interface made by Google, during which they will be able to select which account they want to log in on (or link a new already existing account). They also have the option to create a new account (in which case they will be guided through some setup steps that are also inside the Google-made interface).

## Lúcás
The main thing I worked on this week was adding a cache (PR #73), to be able
to see items offline. This took me quite a bit longer than expected, because
I had to research and figure out how to architect things well. So I ended up
using 5 hours instead of 3.

Thankfully, my next task was adding a sold status to items. I decided
to just make the simplest possible version of this, to be expanded upon
with later features. This only took me 2 hours instead of the estimated 4.

I think my estimates are starting to get a bit high for more basic tasks,
but it's still hard to figure out how long tasks requiring learning new systems
or things will take.

## Samuel

## Sebastian
I added a location to items, with the possibility to fetch the current location of the device through a button. 

With the research and trials-failures, this feature took me around 4 hours. However, I had to spent at least fours hours refactoring the code (and make codeclimate/Cirrus acceots the changes) as some new parts were very similar with what already was implemented for the map feature. 

I started the feature where the items are displayed on the map.

Overall, my time estimates were not accurate.

## Zach
This week I implemented the basic search algorithm, both for text and for categories. Went through a few different algorithms and still needs improvement but for now it works. This work took be about 9 hours altogether.

Next week I want to improve the implementation and try to implement a chat system.

My time estimations are getting better but I would like to implement tasks that require a bit more learning and research.

## Overall team
We did two stand-up meetings this week: on Monday and Thursday. 

The first one was not really useful as most of the members hadn't worked on the project yet.

Some members had problems with Cirrus CI and codeclimate, but we managed to make it work in the end. Everyone participated in at least a review, and all members implemented at least a feature.

Almost all the work that was supposed to be done this week was finished, but not all all since we lost a lot of time because of the CI constantly failing for no apparent reason.
