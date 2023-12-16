StripeGate
=====

Simplify your Stripe payment gateway implementation with StripeGate,<br>
A lightweight Android library that condenses the complexity of integrating Stripe into just a few lines of code.

![](https://github.com/0xRahad/StripeGate/blob/master/app/src/StripeGate.png)

## Features

- Easy integration: Implement Stripe payments with just a few lines of code.
- Minimal configuration: Set up your payment flow effortlessly using a simplified API.
- Efficient and lightweight: Keep your codebase clean and concise with a library designed for simplicity.
- Stripe SDK Integration: Built on top of the official Stripe Android SDK for reliability and security.


## Usage

1. Add the dependency to your project:


   ```gradle
   implementation 'com.github.0xRahad:StripeGate:2.0.0'
   ```

2. Add it in your root settings.gradle:
   
   ```settings.gradle
   maven { url 'https://jitpack.io' }
   ```

## Initialize StripeGate in your activity or application class

   ```java
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.payBtn);
        
        //Create Object and pass the context
        StripeGate stripeGate = new StripeGate(this);


        //Set your own publishable and secret key
        StripeGate.setPublishableKey("pk_test_.............");
        StripeGate.setSecretKey("sk_test_..................");
        
        //Add amount and currency
        stripeGate.Integrate("1000","usd");
        
        //now apply this into Pay btn
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stripeGate.Apply();
            }
        });
   ```

## Contribution
If you wish to contribute a change to any of the existing features or add new to this repo,
please feel free to contribute,
and send a [pull request](https://github.com/0xRahad/StripeGate). I welcome and encourage all pull requests. It usually takes me within 24-48 hours to respond to any issue or request.


## Author
@0xRahad - Available on Linkedln & Facebook
