# Spring Security Core :  Two Factor Authentication

- **Two Factor Authentication** is a type of **multi-factor** **authentication** , also called **2FA**.
- **2FA Authentication** requires the **user** to present **two** or more **authentication factors**.
- **Goal** is to prevent **unauthorized account access** from **account password** being **compromised**.
- Could be as simple as **username, password**, and **code sent** via **text message**, thus, **user needs** to know **password** and have **device receiving** text message **code**.
- **2FA** should use two different **Authentication factors**.

**Type of authentication factors**:

- **Something You Have** : A **bank card**, a **USB Key** with **code**, **FOB** with **code**.
- **Something You Know** : Knowledge of the **user**; **password**, **PIN**, **security question**.
- **Something You Are** : Biometric; **fingerprint**, **iris** or **face scan**.
- **Somewhere You Are** : A **location physical**, or **GPS** based.


**Time-Based One-Time Password**:

- **Time-Based** **One-Time** **Password** : Unique **code**, **valid** for ~**30 seconds**, aka **TOTP**.
- Adopted by Internet Engineering Task Force (IETF) under RFC 6328.
- **Algorithm** based on **Unix time**: **Integer** of **seconds** since **January 1, 1970** (dropping any leap seconds)
- Uses a **shared** `secret`, which if **compromised** will allow **attacker** to **generate codes**.

Google Authenticator
---------

- **Open Source TOTP** generator for **Android** or **iOS**: Allows user to easily **setup TOTP** via scanning a **QR Code**.
- **QR Code** generated using 
	- `Label` : **Account Name** (username)
	- `Secret` : **Arbitrary key** value **Base32 encoded** (shared secret, should be protected): Unique to user.
- **Issuer** : Organization Issuing **TOTP**.

**Example of QR Code Generation URI**
- `otpauth://TYPE/LABEL?PARAMETERS`
- `otpauth://totp/ACME%20Co:john@example.com?secret=HXDMVJECJJWSRB3HWIZR4IFUGFTMXBOZ&issuer=ACME%20Co&algorithm=SHA1&digits=6&period=30`



Google Authenticator with Spring Security
---------

- **Goal**: Configure **Spring Security** to use **Google Authenticator** for **2FA**.
- **Google Authenticator Registration**: Scan **QR Code** to register application.
- Provide User Ability to **opt-in** to **2FA**.
- **Users** who have opted into **2FA** required to enter **2FA code** after **login**.
- **Users** **not opted in**, do **not need** to enter **code**.

Spring Security Configuration
----------
- **Update User Entity** to **hold**:
	- Use **2Fa?**: Is user registered for **2FA**.
	- 2FA **Secret**: **Shared Secret** with Google Authenticator.
	- 2FA **Required?**: **Transient** Property to **require** entering of **2FA code**.
- **Spring Security 2FA Filter** : **On Login**, if User is **2FA enabled**, **forward** to **2FA** Authentication **URL**.


2FA Registration
----------
- Add **Menu** Option and page to **Enable 2FA**.
- **Controller** will **update User** with **2FA secret** and show **QR code**.
- **Accept Form Pos**t of **TOPT code** from Google Authenticator:
	- If **valid**, **enable 2FA** for user.
	- **Redirect** to **index**.

2FA Login
-------
- **Accept** **username** **password** as normal.
- If user is **2FA enabled forward** to **2FA verification page**.
- Use Spring Security **Filter** for **forward**.
- Filter will **restrict user** to **verification** page until proper **code** is **entered**.


> This Example is used for Spring MVC, will work for most typical web applications (Traditional Spring MVC application), it's also suitable for Single Page Javascript, e.g. Angular, ReactJS,...

