

public class MainActivity2 extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "4060e862-b2d1-4da4-bd85-e8ef3188ee09";

    private DuoDrawerLayout drawerLayout;

    public static RecyclerView listChats;


    private Timer mTimer;

    private View mView;
    Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();





        TextView showAdsAfterTextView = findViewById(R.id.showAdsAfterTextView);
        TextView PremiumPromptLimit = findViewById(R.id.premiumPromptLimit);

        if (isPremium) {
            // If isPremium is true, show the premium activity
            showAdsAfterTextView.setVisibility(View.GONE);
            PremiumPromptLimit.setVisibility(View.VISIBLE);

        } else {
            // If isPremium is false, show the regular activity
            showAdsAfterTextView.setVisibility(View.VISIBLE);
            PremiumPromptLimit.setVisibility(View.GONE);
        }


        mDialog = new Dialog(MainActivity2.this);
        showAdsAfterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromptPopup();
            }
        });



        CardView share = findViewById(R.id.shareBtn);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String body = "Check out this amazing app - https://bychat.page.link/app";
                String sub = "ByChat";
                myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                myIntent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(myIntent, "Share Using"));
            }
        });


        // One Signal
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();



        TextView userNameTxt = findViewById(R.id.userName);
        userNameTxt.setText(userName);


        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                Window window = this.getWindow();
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.toolbar));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                Window window1 = this.getWindow();
                window1.setStatusBarColor(ContextCompat.getColor(this, R.color.toolbar));
                break;

            case Configuration.UI_MODE_NIGHT_UNDEFINED:

                break;
        }

        CardView chatsBtn = findViewById(R.id.chatsBtn);
        chatsBtn.setOnClickListener(v-> {
            startActivity(new Intent(MainActivity2.this, ChatsActivity.class));
        });

        CardView btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener( v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        ImageView userImage = findViewById(R.id.userImage);
        Glide.with(MainActivity2.this)
                .load(getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("PROFILE", ""))
                .placeholder(R.drawable.jarvis)
                .into(userImage);

        listChats = findViewById(R.id.listChats);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        listChats.setLayoutManager(manager);

        CardView logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        });

        getData(this);


        //Point timer
        startTimer();
    }

    //Point System Start
    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateShowAdsAfterTextView();
                        //updateShowAdsAfterTextViewPrompt();
                    }
                });
            }
        }, 0, 1000); // Update every second
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void updateShowAdsAfterTextView() {
        TextView showAdsAfterTextView = findViewById(R.id.showAdsAfterTextView);
        showAdsAfterTextView.setText("Prompts Left: " + Constants.showAdsAfter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    //Point System End


    public void PromptPopup() {
        TextView promptPoint;
        Button watchAd;
        Button get_premium;

        mDialog.setContentView(R.layout.prompt_popup);
        promptPoint = (TextView) mDialog.findViewById(R.id.promptPoint);
        watchAd = (Button) mDialog.findViewById(R.id.watchAd);
        get_premium = (Button) mDialog.findViewById(R.id.get_premium);


        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        promptPoint.setText("" + Constants.showAdsAfter);
                    }
                });
            }
        }, 0, 1000);

        watchAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                if (context instanceof ContextWrapper) {
                    Activity activity = (Activity) ((ContextWrapper) context).getBaseContext();
                    if (activity != null) {
                      
                      //Load Ads
                        loadMobileAds(activity);
                        loadRewardAds(activity);
                    }
                }
            }
        });

        mDialog.show();


    }



    public static void getData (Context context) {
        ArrayList<ChatHistoryModel> models = new ArrayList<>();
        Database db = new Database(context);
        models = db.getChatHistoryDB();
        listChats.setAdapter(new ChatHistoryAdapter(models));
    }

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        ImageView premium = findViewById(R.id.premium);
        premium.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity2.this, PremiumActivity.class));
        });

        drawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
        DuoDrawerToggle drawerToggle = new DuoDrawerToggle(this, drawerLayout, toolbar,
                nl.psdcompany.psd.duonavigationdrawer.R.string.navigation_drawer_open,
                nl.psdcompany.psd.duonavigationdrawer.R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(drawerToggle);

        drawerToggle.syncState();

        View contentView = drawerLayout.getContentView();
        View menuView = drawerLayout.getMenuView();
        replace(new HomeFragment());

    }

    private void replace(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.commit();
    }
}
