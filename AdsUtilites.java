public class AdsUtilites {

    NativeAd nativeAd;

    public static void loadMobileAds (Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
    }

    static RewardedAd rewardedAd;



    public static void loadRewardAds(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, activity.getResources().getString(R.string.admob_reward),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

        SharedPreferences preferences = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
        showAdsAfter = preferences.getInt("showAdsAfter", 5);

        if (rewardedAd != null) {
            Activity activityContext = activity;
            rewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    showAdsAfter += 3;
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putInt("showAdsAfter", showAdsAfter);
                    edit.apply();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
        }
    }
}
