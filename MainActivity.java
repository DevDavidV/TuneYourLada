package com.solidpeakdevelopment.tuneyourlada;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;

public class MainActivity extends AppCompatActivity {
    // Screen properties
    int screenHeight, screenWidth;

    // Selected options
    String selectedOptionOptionsBar;
    String selectedOptionMainMenuBar;
    CanvasState tuningOptions = new CanvasState();
    LinearLayout optionbarLayout;

    // Button remembering current height
    TextView globalButton;

    // Variable holding state of current viewing mode
    Boolean photoMode = false;

    // Ad variables
    private FrameLayout adContainerView;
    private int adContainerViewWidth;
    InterstitialAd mInterstitialAd;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Setup main activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets screen properties
        getScreenProperties();

        // Sets the fullscreen mode and etc.
        setupScreen();

        // Sets the sizes so they are calculated off of the actual size of current device
        setupSizes();

        // Sets up buttons of the main menu
        setupMainMenu();

        // Sets up buttons of the option bar menu
        setupOptionsBar();

        // Sets up ads
        setupAds();
    }

    // Gets the size of current screen
    private void getScreenProperties(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    // Sets up screen after resuming back to app
    @Override
    protected void onResume() {
        super.onResume();
        setupScreen();
    }

    // Sets up flags of the screen -> setting it to fullscreen with no navigation bar and permanent landscape mode
    private void setupScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // If older API
        if (Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            // For higher api versions
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    // Sets up advertisements matching the size of screen
    private void setupAds(){
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = outMetrics.density;

        adContainerView = findViewById(R.id.adViewContainer);
        adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.adaptive_banner_ad_unit_id));
        adContainerView.addView(adView);

        float adWidthPixels = adContainerViewWidth;
        int adWidth = (int) (adWidthPixels / density);
        AdSize adSize = AdSize.getCurrentOrientationBannerAdSizeWithWidth(this, adWidth);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(adSize);
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitialAdId));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });
    }

    // Changes ad state when going into photomode
    private void changeAdState(boolean state){
        if(state == true){
            AdRequest adRequest = new AdRequest.Builder().build();

            adView.loadAd(adRequest);
            adView.setVisibility(View.VISIBLE);
        }
        if(state == false){
            adView.destroy();
            adView.setVisibility(View.GONE);
        }
    }

    // Switch between photoMode on and off
    private  void photoMode(){
        if(photoMode==false) {
            changeAdState(false);

            LinearLayout screenWrapper = findViewById(R.id.screenWrapper);
            LinearLayout layout = findViewById(R.id.optionsBar);
            LinearLayout mainMenu = findViewById(R.id.mainMenu);
            ConstraintLayout canvasLayout = findViewById(R.id.mainCanvas);

            int bgImageId = getResources().getIdentifier("background", "drawable", getPackageName());

            layout.setVisibility(View.GONE);
            mainMenu.setVisibility(View.GONE);
            canvasLayout.setBackgroundResource(0);
            screenWrapper.setBackgroundResource(bgImageId);
        }else if(photoMode==true){
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }

            changeAdState(true);

            LinearLayout screenWrapper = findViewById(R.id.screenWrapper);
            LinearLayout layout = findViewById(R.id.optionsBar);
            LinearLayout mainMenu = findViewById(R.id.mainMenu);
            ConstraintLayout canvasLayout = findViewById(R.id.mainCanvas);

            int bgImageId = getResources().getIdentifier("background", "drawable", getPackageName());

            layout.setVisibility(View.VISIBLE);
            mainMenu.setVisibility(View.VISIBLE);
            canvasLayout.setBackgroundResource(bgImageId);
            screenWrapper.setBackgroundResource(0);

        }

        photoMode = !photoMode;
    }

    // Creates and sets up main buttons
    private void setupMainMenu() {
        LinearLayout colorButton = findViewById(R.id.colorButton);
        LinearLayout wheelsButton = findViewById(R.id.wheelsButton);
        LinearLayout windowTints = findViewById(R.id.windowsTintButton);
        LinearLayout spoilerButton = findViewById(R.id.spoilersButton);
        LinearLayout bodyKitsButton = findViewById(R.id.bodykitsButton);
        LinearLayout suspensionButton = findViewById(R.id.suspensionButton);
        ImageView photoModeSwitcher = findViewById(R.id.photoModeSwitcher);

        setFunctionality(colorButton, "setupColorButton");
        setFunctionality(wheelsButton, "setupWheelsButton");
        setFunctionality(windowTints, "setupWindowTintsButton");
        setFunctionality(spoilerButton, "setupSpoilersButton");
        setFunctionality(bodyKitsButton, "setupBodykitsButton");
        setFunctionality(suspensionButton, "setupSuspensionButton");
        setFunctionality(photoModeSwitcher, "goPhotoMode");
    }

    // Clicks on first button -> default option at start
    private void setupOptionsBar() {
        optionbarLayout = findViewById(R.id.optionsBar);
        final LinearLayout colorButton = findViewById(R.id.colorButton);
        colorButton.performClick();
    }

    // Changes option bar to selection of colours
    private void changeOptionsbarToColor() {
        LinearLayout layout = findViewById(R.id.optionsBar);
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        layout.setPadding(0,doubleToInt(screenHeight / 15),0,0);

        layout.addView(createButton("colorToRed","red",buttonParameters));
        layout.addView(createButton("colorToWhite","white",buttonParameters));
        layout.addView(createButton("colorToBlack","black",buttonParameters));
        layout.addView(createButton("colorToGreen","green",buttonParameters));
        layout.addView(createButton("colorToBlue","blue",buttonParameters));
        layout.addView(createButton("colorToOrange","orange",buttonParameters));
        layout.addView(createButton("colorToPurple","purple",buttonParameters));
        layout.addView(createButton("colorToGold","gold",buttonParameters));
    }

    // Changes option bar to selection of wheels
    private void changeOptionsbarToWheels() {
        LinearLayout layout = findViewById(R.id.optionsBar);
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        layout.setPadding(0,doubleToInt(screenHeight / 15),0,0);


        layout.addView(createButton("wheelTo0","iwheel0",buttonParameters));
        layout.addView(createButton("wheelTo1","iwheel1",buttonParameters));
        layout.addView(createButton("wheelTo2","iwheel2",buttonParameters));
        layout.addView(createButton("wheelTo3","iwheel3",buttonParameters));
        layout.addView(createButton("wheelTo4","iwheel4",buttonParameters));
        layout.addView(createButton("wheelTo5","iwheel5",buttonParameters));
        layout.addView(createButton("wheelTo6","iwheel6",buttonParameters));
        layout.addView(createButton("wheelTo7","iwheel7",buttonParameters));

    }

    // Changes option bar to selection of windows tints
    private void changeOptionsbarToWindowTints() {
        LinearLayout layout = findViewById(R.id.optionsBar);
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        layout.setPadding(0,doubleToInt(screenHeight / 15),0,0);

        layout.addView(createCustomButton("tintToTransparent","transparent",buttonParameters,screenWidth/6, screenHeight * 0.145));
        layout.addView(createCustomButton("tintToLight","light",buttonParameters,screenWidth/6, screenHeight * 0.145));
        layout.addView(createCustomButton("tintToDark","dark",buttonParameters,screenWidth/6, screenHeight * 0.145));
        layout.addView(createCustomButton("tintToLimo","limo",buttonParameters,screenWidth/6, screenHeight * 0.145));

    }

    // Changes option bar to selection of spoilers
    private void changeOptionsbarToSpoilers() {
        LinearLayout layout = findViewById(R.id.optionsBar);
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        layout.setPadding(doubleToInt(screenWidth / 8),doubleToInt(screenHeight / 15),doubleToInt(screenWidth / 8),0);

        layout.addView(createButton("spoilerToNospoiler","nospoiler",buttonParameters));
        layout.addView(createButton("spoilerToLip","lip",buttonParameters));
        layout.addView(createButton("spoilerToHighspoiler","highspoiler",buttonParameters));
        layout.addView(createButton("spoilerToBigspoiler","bigspoiler",buttonParameters));
        layout.addView(createButton("spoilerToPlasticlip","plasticlip",buttonParameters));


    }

    // Changes option bar to selection of bodykits
    private void changeOptionsbarToBodykits() {
        LinearLayout layout = findViewById(R.id.optionsBar);
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        layout.setPadding(doubleToInt(screenWidth / 20),doubleToInt(screenHeight / 15),doubleToInt(screenWidth / 20),0);

        layout.addView(createCustomButton("bodykitTo0","bodykit0",buttonParameters,screenWidth/5.5, screenHeight * 0.145));
        layout.addView(createCustomButton("bodykitTo1","bodykit1",buttonParameters,screenWidth/5.5, screenHeight * 0.145));
        layout.addView(createCustomButton("bodykitTo2","bodykit2",buttonParameters,screenWidth/5.5, screenHeight * 0.145));
    }

    // Changes option bar to selection of suspension mods
    private void changeOptionsbarToSuspenison() {
        LinearLayout layout = findViewById(R.id.optionsBar);
        LinearLayout.LayoutParams buttonParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT, 1f);

        layout.setPadding(0,doubleToInt(screenHeight / 15),0,0);

        layout.addView(createButton("suspensionToPlus","plus", buttonParameters));
        layout.addView(createSuspensionCounterButton("padding",buttonParameters,screenWidth/4, screenHeight * 0.145));
        layout.addView(createButton("suspensionToMinus","minus", buttonParameters));
        layout.addView(createButton("suspensionToLow","lowsusp", buttonParameters));
        layout.addView(createButton("suspensionToMedium","mediumsusp", buttonParameters));
        layout.addView(createButton("suspensionToHigh","highsusp", buttonParameters));
    }


    //Main switch switching all the menus and options
    private void setFunctionality(View object, final String type) {
        final String objectType = type;

        object.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((selectedOptionOptionsBar != objectType)&&(selectedOptionMainMenuBar != objectType)) {
                    switch (objectType) {
                        case "setupColorButton":
                            optionbarLayout.removeAllViews();
                            changeOptionsbarToColor();
                            selectedOptionMainMenuBar = objectType;
                            break;
                        case "setupWheelsButton":
                            optionbarLayout.removeAllViews();
                            changeOptionsbarToWheels();
                            selectedOptionMainMenuBar = objectType;
                            break;
                        case "setupWindowTintsButton":
                            optionbarLayout.removeAllViews();
                            changeOptionsbarToWindowTints();
                            selectedOptionMainMenuBar = objectType;
                            break;
                        case "setupSpoilersButton":
                            optionbarLayout.removeAllViews();
                            changeOptionsbarToSpoilers();
                            selectedOptionMainMenuBar = objectType;
                            break;
                        case "setupBodykitsButton":
                            optionbarLayout.removeAllViews();
                            changeOptionsbarToBodykits();
                            selectedOptionMainMenuBar = objectType;
                            break;
                        case "setupSuspensionButton":
                            optionbarLayout.removeAllViews();
                            changeOptionsbarToSuspenison();
                            selectedOptionMainMenuBar = objectType;
                            break;

                        case "colorToRed":
                            tuningOptions.setBodyColor("red");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToWhite":
                            tuningOptions.setBodyColor("white");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToBlack":
                            tuningOptions.setBodyColor("black");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToGreen":
                            tuningOptions.setBodyColor("green");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToBlue":
                            tuningOptions.setBodyColor("blue");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToOrange":
                            tuningOptions.setBodyColor("orange");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToPurple":
                            tuningOptions.setBodyColor("purple");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "colorToGold":
                            tuningOptions.setBodyColor("gold");
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo0":
                            tuningOptions.setWheels(0);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo1":
                            tuningOptions.setWheels(1);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo2":
                            tuningOptions.setWheels(2);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo3":
                            tuningOptions.setWheels(3);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo4":
                            tuningOptions.setWheels(4);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo5":
                            tuningOptions.setWheels(5);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo6":
                            tuningOptions.setWheels(6);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "wheelTo7":
                            tuningOptions.setWheels(7);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "tintToTransparent":
                            tuningOptions.setWindowTint(0);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "tintToLight":
                            tuningOptions.setWindowTint(1);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "tintToDark":
                            tuningOptions.setWindowTint(2);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "tintToLimo":
                            tuningOptions.setWindowTint(3);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "spoilerToNospoiler":
                            tuningOptions.setSpoiler(0);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "spoilerToLip":
                            tuningOptions.setSpoiler(1);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "spoilerToHighspoiler":
                            tuningOptions.setSpoiler(2);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "spoilerToBigspoiler":
                            tuningOptions.setSpoiler(3);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "spoilerToPlasticlip":
                            tuningOptions.setSpoiler(4);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "bodykitTo0":
                            tuningOptions.setBodykit(0);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "bodykitTo1":
                            tuningOptions.setBodykit(1);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "bodykitTo2":
                            tuningOptions.setBodykit(2);
                            updateTuningCanvas();
                            selectedOptionOptionsBar = objectType;
                            break;
                        case "suspensionToPlus":
                            tuningOptions.plusSuspension();
                            updateTuningCanvas();
                            updateSuspensionCounter();
                            break;
                        case "suspensionToMinus":
                            tuningOptions.minusSuspension();
                            updateTuningCanvas();
                            updateSuspensionCounter();
                            break;
                        case "suspensionToLow":
                            tuningOptions.setLowSuspension();
                            updateTuningCanvas();
                            updateSuspensionCounter();
                            break;
                        case "suspensionToMedium":
                            tuningOptions.setMediumSuspension();
                            updateTuningCanvas();
                            updateSuspensionCounter();
                            break;
                        case "suspensionToHigh":
                            tuningOptions.setHighSuspension();
                            updateTuningCanvas();
                            updateSuspensionCounter();
                            break;
                        case "goPhotoMode":
                            photoMode();
                        default:
                            break;

                    }
                }
            }
        });
    }

    // Function to create default button
    private LinearLayout createButton(String type, String imageName, LinearLayout.LayoutParams layoutParameters){
        int buttonImageId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        int sizeOfButton = doubleToInt(screenHeight * 0.145);

        LinearLayout wrapper = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams wrapperParameters = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapper.setGravity(Gravity.CENTER);
        wrapper.setLayoutParams(layoutParameters);

        ImageView newButton = new ImageView(getApplicationContext());
        newButton.setLayoutParams(wrapperParameters);
        newButton.setBackgroundResource(buttonImageId);
        newButton.getLayoutParams().height = sizeOfButton;
        newButton.getLayoutParams().width = sizeOfButton;

        setFunctionality(newButton, type);

        wrapper.addView(newButton);

        return wrapper;
    }

    // Function to create default custom button
    private View createCustomButton(String type, String imageName, LinearLayout.LayoutParams layoutParameters, double width, double height){
        int heightOfButton = doubleToInt(height);
        int widthOfButton = doubleToInt(width);

        LinearLayout wrapperWithButton = createButton(type, imageName, layoutParameters);
        ImageView newButton = (ImageView) wrapperWithButton.getChildAt(0);
        newButton.getLayoutParams().height = heightOfButton;
        newButton.getLayoutParams().width = widthOfButton;

        return wrapperWithButton;
    }

    // Function to create default button showing suspension height
    private View createSuspensionCounterButton(String imageName, LinearLayout.LayoutParams layoutParameters, double width, double height){
        int buttonImageId = getResources().getIdentifier(imageName, "drawable", getPackageName());
        int heightOfButton = doubleToInt(height);
        int widthOfButton = doubleToInt(width);

        globalButton = new TextView(getApplicationContext());
        globalButton.setBackgroundResource(buttonImageId);

        Typeface textFont = ResourcesCompat.getFont(getApplicationContext(), R.font.russo_one);
        int textSizing = doubleToInt(screenHeight*0.03);
        globalButton.setTypeface(textFont);
        globalButton.setSingleLine();
        globalButton.setTextColor(Color.parseColor("#ffffff"));
        globalButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);

        globalButton.setText(tuningOptions.getHeightText()+" cm");

        globalButton.setGravity(Gravity.CENTER);
        globalButton.setLayoutParams(layoutParameters);

        globalButton.getLayoutParams().width = doubleToInt(widthOfButton);
        globalButton.getLayoutParams().height = doubleToInt(heightOfButton);

        return globalButton;
    }


    // Function to create change the value of button showing suspension height
    private void updateSuspensionCounter(){
        globalButton.setText(tuningOptions.getHeightText()+" cm");
    }

    // Updates the canvas to current changes
    private void updateTuningCanvas() {
        ImageView carBody = findViewById(R.id.car);
        ImageView frontWheel = findViewById(R.id.wheelFront);
        ImageView rearWheel = findViewById(R.id.wheelRear);
        ImageView windowTintFront = findViewById(R.id.windowTintFront);
        ImageView behindWheel = findViewById(R.id.behindwheel);
        ConstraintLayout canvasLayout = findViewById(R.id.mainCanvas);

        ConstraintSet canvasLayoutSet = new ConstraintSet();
        canvasLayoutSet.clone(canvasLayout);

        String carBodyImageName = tuningOptions.getBodyColor() + tuningOptions.getBodykit() + tuningOptions.getSpoiler();
        String wheelImageName = "wheel" + tuningOptions.getWheels();
        String windowTintFrontName = "ftint" + tuningOptions.getWindowTint();

        int carBodyImageId = getResources().getIdentifier(carBodyImageName, "drawable", getPackageName());
        int wheelImageId = getResources().getIdentifier(wheelImageName, "drawable", getPackageName());
        int windowTintFrontImageId = getResources().getIdentifier(windowTintFrontName, "drawable", getPackageName());


        carBody.setImageDrawable(getResources().getDrawable(carBodyImageId));
        frontWheel.setImageDrawable(getResources().getDrawable(wheelImageId));
        rearWheel.setImageDrawable(getResources().getDrawable(wheelImageId));
        windowTintFront.setImageDrawable(getResources().getDrawable(windowTintFrontImageId));


        int carBodyHeight = doubleToInt(screenWidth * tuningOptions.getSuspensionHeight());
        canvasLayoutSet.setMargin(carBody.getId(), ConstraintSet.BOTTOM, carBodyHeight);
        canvasLayoutSet.setMargin(windowTintFront.getId(), ConstraintSet.BOTTOM, carBodyHeight);
        canvasLayoutSet.setMargin(behindWheel.getId(), ConstraintSet.BOTTOM, (2 * carBodyHeight) / 3);
        canvasLayoutSet.applyTo(canvasLayout);

    }

    // Sets up sizes corresponding to actual screen size
    private void  setupSizes(){
        ImageView carBody = findViewById(R.id.car);
        ImageView frontWheel = findViewById(R.id.wheelFront);
        ImageView rearWheel = findViewById(R.id.wheelRear);
        ImageView windowTintFront = findViewById(R.id.windowTintFront);
        ImageView behindWheel = findViewById(R.id.behindwheel);
        ImageView shadow = findViewById(R.id.shadow);
        ImageView modeSwitcher = findViewById(R.id.photoModeSwitcher);
        ConstraintLayout canvasLayout = findViewById(R.id.mainCanvas);
        LinearLayout mainMenu = findViewById(R.id.mainMenu);
        LinearLayout optionBar = findViewById(R.id.optionsBar);
        adContainerView = findViewById(R.id.adViewContainer);

        optionBar.setGravity(Gravity.CENTER_VERTICAL);

        ConstraintSet canvasLayoutSet = new ConstraintSet();
        canvasLayoutSet.clone(canvasLayout);

        int layoutHeight = doubleToInt(screenHeight * 0.73);
        int mainMenuWidth = doubleToInt(screenHeight * 0.2);

        canvasLayout.getLayoutParams().height = layoutHeight;
        mainMenu.getLayoutParams().width = mainMenuWidth;

        TextView colorButton = findViewById(R.id.colorText);
        TextView wheelsButton = findViewById(R.id.wheelsText);
        TextView windowTints = findViewById(R.id.windowsTintText);
        TextView spoilerButton = findViewById(R.id.spoilerText);
        TextView bodyKitsButton = findViewById(R.id.bodyKitsText);
        TextView suspensionButton = findViewById(R.id.suspensionText);


        int textSizing = doubleToInt(screenHeight*0.025);

        colorButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);
        wheelsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);
        windowTints.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);
        spoilerButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);
        bodyKitsButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);
        suspensionButton.setTextSize(TypedValue.COMPLEX_UNIT_PX,(float)textSizing);


        int carHeight = doubleToInt(screenWidth * 0.23);
        int carWidth = doubleToInt(screenWidth * 0.87);
        int wheelSize = doubleToInt(screenWidth * 0.105);
        int behindWheelHeight = doubleToInt(screenWidth * 0.16);
        int behindWheelWidth = doubleToInt(screenWidth * 0.7725);
        int modeSwitcherSize = doubleToInt(screenWidth * 0.06);
        adContainerViewWidth = 6 * mainMenuWidth;

        int carBodyMargin = doubleToInt(screenWidth * tuningOptions.getSuspensionHeight());

        canvasLayoutSet.constrainHeight(carBody.getId(), carHeight);
        canvasLayoutSet.constrainWidth(carBody.getId(), carWidth);
        canvasLayoutSet.setMargin(carBody.getId(), ConstraintSet.BOTTOM, carBodyMargin);

        canvasLayoutSet.constrainHeight(windowTintFront.getId(), carHeight);
        canvasLayoutSet.constrainWidth(windowTintFront.getId(), carWidth);
        canvasLayoutSet.setMargin(windowTintFront.getId(), ConstraintSet.BOTTOM, carBodyMargin);

        canvasLayoutSet.setMargin(frontWheel.getId(), ConstraintSet.END, doubleToInt(screenWidth * 0.54));
        canvasLayoutSet.setMargin(rearWheel.getId(), ConstraintSet.START, doubleToInt(screenWidth * 0.37));

        canvasLayoutSet.constrainWidth(frontWheel.getId(), wheelSize);
        canvasLayoutSet.constrainHeight(frontWheel.getId(), wheelSize);

        canvasLayoutSet.constrainWidth(rearWheel.getId(), wheelSize);
        canvasLayoutSet.constrainHeight(rearWheel.getId(), wheelSize);

        canvasLayoutSet.constrainWidth(behindWheel.getId(), behindWheelWidth);
        canvasLayoutSet.constrainHeight(behindWheel.getId(), behindWheelHeight);
        canvasLayoutSet.setMargin(behindWheel.getId(), ConstraintSet.BOTTOM, (2 * carBodyMargin) / 3);

        canvasLayoutSet.constrainWidth(shadow.getId(), doubleToInt(screenWidth*0.825));
        canvasLayoutSet.constrainHeight(shadow.getId(), doubleToInt(screenHeight*0.125));

        modeSwitcher.getLayoutParams().height = modeSwitcherSize;
        modeSwitcher.getLayoutParams().width = modeSwitcherSize;
        modeSwitcher.setPadding(0,doubleToInt(modeSwitcherSize/3),doubleToInt(modeSwitcherSize/3),0);

        canvasLayoutSet.constrainWidth(adContainerView.getId(),adContainerViewWidth);

        canvasLayoutSet.applyTo(canvasLayout);
        updateTuningCanvas();
    }

    // Function transforming to int from double
    private int doubleToInt(double doubleToTransform){
        int doubleToReturn = (int) doubleToTransform;
        return doubleToReturn;
    }

}