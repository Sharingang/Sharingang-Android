# Week 1

# Lúcás

First I implemented a simple form for creating new items. This took 3 hours instead of the
2 hours I estimated, because I was a bit rustier on Android than I thought, so figuring
out how to do the basics took a bit longer.

Then I worked on creating a list for viewing multiple items. This took me 3 hours instead
of 2, mainly because figuring out how to use a RecyclerView was more complicated
than I anticpated.

I had started working on adding persistence, and basically finished, but then Samuel's
work on Firebase basically upended my work, at least for now.

# Samuel

I did some research about Firebase, and documented my findings.
Then I implemented a repository to store and retrieve items using Firestore.

My time estimate was accurate for the research but for the implementation it took more time.

For this first sprint, we were a bit late to merge all the changes. I had to wait for other tasks before merging but luckily the architecture we had was good and I didn't have to change a lot of code to merge.

# Scrum Master (Lúcás)

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