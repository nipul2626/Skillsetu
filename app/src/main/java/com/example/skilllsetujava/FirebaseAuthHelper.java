package com.example.skilllsetujava;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * 🔐 Firebase Authentication Helper
 *
 * Handles all authentication operations:
 * - Email/Password Sign Up & Sign In
 * - Google Sign-In
 * - Password Reset
 * - Sign Out
 * - User Session Management
 *
 * Usage:
 * FirebaseAuthHelper authHelper = new FirebaseAuthHelper(context);
 * authHelper.signInWithEmail(email, password, callback);
 */
public class FirebaseAuthHelper {

    private static final String TAG = "FirebaseAuthHelper";
    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Context context;

    /**
     * Constructor
     * @param context Application context
     */
    public FirebaseAuthHelper(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        configureGoogleSignIn();
    }

    /**
     * Configure Google Sign-In
     *
     * IMPORTANT: Replace "YOUR_WEB_CLIENT_ID" with your actual Web Client ID
     * Get it from: google-services.json file
     * Look for: "oauth_client" → "client_type": 3 → "client_id"
     */
    private void configureGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1060341212332-1uafaq8l3smnorb002b7t5pvfq8lojck.apps.googleusercontent.com") // TODO: Replace with your Web Client ID
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    // ==================== SIGN UP ====================

    /**
     * ✅ Sign Up with Email & Password
     *
     * @param email User's email address
     * @param password User's password (min 6 characters)
     * @param callback Callback for success/failure
     */
    public void signUpWithEmail(String email, String password, AuthCallback callback) {
        // Validation
        if (email == null || email.trim().isEmpty()) {
            callback.onError("Email cannot be empty");
            return;
        }

        if (!isValidEmail(email)) {
            callback.onError("Invalid email format");
            return;
        }

        if (password == null || password.isEmpty()) {
            callback.onError("Password cannot be empty");
            return;
        }

        if (password.length() < 6) {
            callback.onError("Password must be at least 6 characters");
            return;
        }

        // Create user account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "✅ Sign up successful: " + (user != null ? user.getUid() : "null"));
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Sign up failed";
                        Log.e(TAG, "❌ Sign up failed: " + error);
                        callback.onError(error);
                    }
                });
    }

    // ==================== SIGN IN ====================

    /**
     * ✅ Sign In with Email & Password
     *
     * @param email User's email address
     * @param password User's password
     * @param callback Callback for success/failure
     */
    public void signInWithEmail(String email, String password, AuthCallback callback) {
        // Validation
        if (email == null || email.trim().isEmpty()) {
            callback.onError("Email cannot be empty");
            return;
        }

        if (!isValidEmail(email)) {
            callback.onError("Invalid email format");
            return;
        }

        if (password == null || password.isEmpty()) {
            callback.onError("Password cannot be empty");
            return;
        }

        // Sign in
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "✅ Sign in successful: " + (user != null ? user.getUid() : "null"));
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Sign in failed";
                        Log.e(TAG, "❌ Sign in failed: " + error);
                        callback.onError(error);
                    }
                });
    }

    // ==================== GOOGLE SIGN-IN ====================

    /**
     * 🔵 Start Google Sign-In Flow
     *
     * Call this from an Activity. Then handle the result in onActivityResult()
     *
     * @param activity The calling activity
     */
    public void startGoogleSignIn(Activity activity) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    /**
     * 🔵 Handle Google Sign-In Result
     *
     * Call this from onActivityResult() in your Activity
     *
     * @param requestCode Request code from onActivityResult
     * @param data Intent data from onActivityResult
     * @param callback Callback for success/failure
     */
    public void handleGoogleSignInResult(int requestCode, Intent data, AuthCallback callback) {
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign-In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    String idToken = account.getIdToken();
                    firebaseAuthWithGoogle(idToken, callback);
                } else {
                    callback.onError("Google sign-in failed: Account is null");
                }
            } catch (ApiException e) {
                Log.e(TAG, "❌ Google sign-in failed: " + e.getStatusCode() + " - " + e.getMessage());
                callback.onError("Google sign-in failed: " + e.getMessage());
            }
        }
    }

    /**
     * 🔵 Authenticate with Firebase using Google ID Token
     *
     * @param idToken Google ID Token
     * @param callback Callback for success/failure
     */
    private void firebaseAuthWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "✅ Google authentication successful: " + (user != null ? user.getUid() : "null"));
                        callback.onSuccess(user);
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Google authentication failed";
                        Log.e(TAG, "❌ Google authentication failed: " + error);
                        callback.onError(error);
                    }
                });
    }

    // ==================== SIGN OUT ====================

    /**
     * 🚪 Sign Out
     *
     * Signs out from both Firebase and Google
     *
     * @param callback Callback when sign out is complete
     */
    public void signOut(SignOutCallback callback) {
        // Sign out from Firebase
        mAuth.signOut();

        // Sign out from Google
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Log.d(TAG, "✅ User signed out");
            if (callback != null) {
                callback.onSignOutComplete();
            }
        });
    }

    // ==================== PASSWORD RESET ====================

    /**
     * 🔄 Send Password Reset Email
     *
     * @param email Email address to send reset link
     * @param callback Callback for success/failure
     */
    public void sendPasswordResetEmail(String email, ResetCallback callback) {
        if (email == null || email.trim().isEmpty()) {
            callback.onError("Email cannot be empty");
            return;
        }

        if (!isValidEmail(email)) {
            callback.onError("Invalid email format");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "✅ Password reset email sent to: " + email);
                        callback.onSuccess();
                    } else {
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Failed to send reset email";
                        Log.e(TAG, "❌ Password reset failed: " + error);
                        callback.onError(error);
                    }
                });
    }

    // ==================== USER INFO ====================

    /**
     * 👤 Get Current Logged-in User
     *
     * @return FirebaseUser object or null if not logged in
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * ✅ Check if User is Logged In
     *
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * 📧 Get Current User ID (UID)
     *
     * @return User ID or null if not logged in
     */
    public String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    /**
     * 📧 Get Current User Email
     *
     * @return User email or null if not logged in
     */
    public String getCurrentUserEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    /**
     * 👤 Get Current User Display Name
     *
     * @return User display name or null if not set
     */
    public String getCurrentUserDisplayName() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getDisplayName() : null;
    }

    /**
     * 🖼️ Get Current User Photo URL
     *
     * @return User photo URL or null if not set
     */
    public String getCurrentUserPhotoUrl() {
        FirebaseUser user = getCurrentUser();
        if (user != null && user.getPhotoUrl() != null) {
            return user.getPhotoUrl().toString();
        }
        return null;
    }

    // ==================== HELPER METHODS ====================

    /**
     * ✅ Email Validation
     *
     * @param email Email to validate
     * @return true if valid email format
     */
    private boolean isValidEmail(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * 📤 Get Google Sign-In Request Code
     *
     * Use this in your Activity's onActivityResult to check if the result is from Google Sign-In
     *
     * @return Request code for Google Sign-In
     */
    public static int getGoogleSignInRequestCode() {
        return RC_GOOGLE_SIGN_IN;
    }

    // ==================== CALLBACKS ====================

    /**
     * Callback interface for authentication operations
     */
    public interface AuthCallback {
        /**
         * Called when authentication is successful
         * @param user The authenticated FirebaseUser
         */
        void onSuccess(FirebaseUser user);

        /**
         * Called when authentication fails
         * @param error Error message
         */
        void onError(String error);
    }

    /**
     * Callback interface for sign-out operation
     */
    public interface SignOutCallback {
        /**
         * Called when sign-out is complete
         */
        void onSignOutComplete();
    }

    /**
     * Callback interface for password reset operation
     */
    public interface ResetCallback {
        /**
         * Called when password reset email is sent successfully
         */
        void onSuccess();

        /**
         * Called when password reset fails
         * @param error Error message
         */
        void onError(String error);
    }
}