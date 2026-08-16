# Student Discussion Forum
# Overview
This program is a JavaFX desktop application that simulates a Student Discussion Forum. This program is built on an in-memory H2 database with a role based Model-View-Controller architecture.

This program has three roles: Student, Admin, Reviewer.

Upon starting the program, the first user is automatically assigned the admin role. They would be prompted to create an account using a username and password. Upon logging in, the admin can update their profile and perform the following duties: send invitation codes to new users, send one time passwords to current users for password changes, add or remove roles, delete users and view staff requests.

The students can create, update, view and delete their posts and replies. The posts are organised into categories called threads (General, lecture, Sections, Problem sets, Assignments, Social). The students can also search the posts and replies by a keyword or a thread. They can also see private feedback that is given on their posts by the reviewers.

The reviewers in this forum review the posts and replies to make sure they comply with academic integrity rules. Reviewers can provide private feedback to the students' posts and replies, set up admin requests, and create and delete threads.

This program uses a Finite State Machine (FSM) based username recogniser, plus a real time password strength feedback(uppercase, lowercase, digit, special character, 8-16 length) during account setup.

# Login Flow
User enters username and password on the login page. If the entered password matches an active one time password, the user is required to set a new password via a password popup before they can log in. Otherwise, the password is checked against the stored account password. On success, the number of roles the account holds determines routing:

One role: straight to that role's home page.

Multiple roles: a role selection page lets the user choose which role to use for the session.

From any role's home page, the user can open the User Update page to edit their name, email or password, then return to their home page.


