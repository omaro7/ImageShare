package kr.co.goms.tarot.dialog;

import android.animation.Animator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import androidx.core.content.FileProvider;
import kr.co.goms.tarot.AppConstant;
import kr.co.goms.tarot.R;
import kr.co.goms.tarot.model.TarotCardS;
import kr.co.goms.tarot.util.GomsLog;
import kr.co.goms.tarot.util.StringUtil;
import kr.co.goms.tarot.util.ToastUtil;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class GomsRequestDialog extends Dialog implements OnClickListener
{
    private TextView mTitle;
    private Button           mOK;
    private boolean          mBackPress  = true;

    LinearLayout mLltTarotSpread;
    ImageButton mBtnReveal;
    LinearLayout mRevealView, mLayoutButtons;
    Animation mAlphaAnimation;
    float pixelDensity;
    boolean flag = true;

    private ImageView mIvTarotPast;
    private ImageView mIvTarotPresent;
    private ImageView mIvTarotFuture;
    private TextView mTvTarotPast;
    private TextView mTvTarotPresent;
    private TextView mTvTarotFuture;

    private Button mBtnShare, mBtnSave, mBtnConsulting;

    HashMap<Integer, TarotCardS> mTarotCardHashMap;

    private IMDialogListener mOkCallback = null;

    /**
     * Common Dialog Listener
     *
     * @author minsus
     */
    public interface IMDialogListener
    {
        void onClickOk(String question);
    }

    public GomsRequestDialog setOnClickOk(IMDialogListener cb)
    {
        mOkCallback = cb;
        return this;
    }

    public GomsRequestDialog setTarotCard(HashMap<Integer, TarotCardS> tarotCardHashMap){
        mTarotCardHashMap = tarotCardHashMap;

        Glide.with(getContext()).load(mTarotCardHashMap.get(1).getTarotCardImageThumb()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mIvTarotPast);
        Glide.with(getContext()).load(mTarotCardHashMap.get(2).getTarotCardImageThumb()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mIvTarotPresent);
        Glide.with(getContext()).load(mTarotCardHashMap.get(3).getTarotCardImageThumb()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(mIvTarotFuture);
        StringBuffer sb = new StringBuffer();
        sb.append(mTarotCardHashMap.get(1).getTarotCardName());
        sb.append("\n");
        sb.append(mTarotCardHashMap.get(1).getTarotCardNameEn());
        mTvTarotPast.setText(sb.toString());

        StringBuffer sb2 = new StringBuffer();
        sb2.append(mTarotCardHashMap.get(2).getTarotCardName());
        sb2.append("\n");
        sb2.append(mTarotCardHashMap.get(2).getTarotCardNameEn());
        mTvTarotPresent.setText(sb2.toString());

        StringBuffer sb3 = new StringBuffer();
        sb3.append(mTarotCardHashMap.get(3).getTarotCardName());
        sb3.append("\n");
        sb3.append(mTarotCardHashMap.get(3).getTarotCardNameEn());
        mTvTarotFuture.setText(sb3.toString());

        return this;
    }

    public GomsRequestDialog(Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_goms_request);

        mTitle = findViewById(R.id.tv_tarot_question);

        pixelDensity = context.getResources().getDisplayMetrics().density;
        mLltTarotSpread = (LinearLayout) findViewById(R.id.iv_tarot_spread);
        mBtnReveal = (ImageButton) findViewById(R.id.ib_reveal);
        mRevealView = (LinearLayout) findViewById(R.id.llt_reveal);
        mLayoutButtons = (LinearLayout) findViewById(R.id.layoutButtons);

        mBtnShare = findViewById(R.id.btn_share);
        mBtnSave = findViewById(R.id.btn_save);
        mBtnConsulting = findViewById(R.id.btn_consulting);

        mAlphaAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_anim);

        mIvTarotPast = findViewById(R.id.iv_tarot_past);                //1
        mIvTarotPresent = findViewById(R.id.iv_tarot_present);          //2
        mIvTarotFuture = findViewById(R.id.iv_tarot_future);            //3

        mTvTarotPast = findViewById(R.id.tv_tarot_past);
        mTvTarotPresent = findViewById(R.id.tv_tarot_present);
        mTvTarotFuture = findViewById(R.id.tv_tarot_future);
        
        LayoutParams params = getWindow().getAttributes();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((WindowManager.LayoutParams) params);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        
      //getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

        mOK = (Button) findViewById(R.id.dialog_common_ok);

        mOK.setOnClickListener(this);
        mBtnReveal.setOnClickListener(this);
        mBtnShare.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mBtnConsulting.setOnClickListener(this);
    }

    private void goReveal() {

        /*
         MARGIN_RIGHT = 16;
         FAB_BUTTON_RADIUS = 28;
         */
        int x = mLltTarotSpread.getRight();
        int y = mLltTarotSpread.getBottom();
        x -= ((28 * pixelDensity) + (16 * pixelDensity));

        int hypotenuse = (int) Math.hypot(mLltTarotSpread.getWidth(), mLltTarotSpread.getHeight());

        if (flag) {

            mBtnReveal.setBackgroundResource(R.drawable.rounded_cancel_button);
            mBtnReveal.setImageResource(R.drawable.image_cancel);

            FrameLayout.LayoutParams parameters = (FrameLayout.LayoutParams)mRevealView.getLayoutParams();
            parameters.height = mLltTarotSpread.getHeight();
            mRevealView.setLayoutParams(parameters);

            Animator anim = ViewAnimationUtils.createCircularReveal(mRevealView, x, y, 0, hypotenuse);
            anim.setDuration(500);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mLayoutButtons.setVisibility(View.VISIBLE);
                    mLayoutButtons.startAnimation(mAlphaAnimation);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            mRevealView.setVisibility(View.VISIBLE);
            anim.start();

            flag = false;
        } else {

            mBtnReveal.setBackgroundResource(R.drawable.rounded_button);
            mBtnReveal.setImageResource(R.drawable.baseline_share_white_18dp);

            Animator anim = ViewAnimationUtils.createCircularReveal(mRevealView, x, y, hypotenuse, 0);
            anim.setDuration(400);

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mRevealView.setVisibility(View.GONE);
                    mLayoutButtons.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            anim.start();
            flag = true;
        }
    }

    private void goShare(){

        mLltTarotSpread.setDrawingCacheEnabled(true);
        mLltTarotSpread.buildDrawingCache();
        Bitmap cache = mLltTarotSpread.getDrawingCache();

        File cachePath = new File(getContext().getCacheDir(), "images");
        cachePath.mkdirs(); // don't forget to make the directory

        String targetFileName = AppConstant.PHOTO_STARTWITH + "_" + System.currentTimeMillis()+".jpg";

        try {

            File file = new File(cachePath +"/" + targetFileName );

            FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath());
            cache.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

            Uri uri = FileProvider.getUriForFile(getContext(),AppConstant.APP_URI + ".provider", file);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/jpeg");
            getContext().startActivity(Intent.createChooser(shareIntent, getContext().getResources().getText(R.string.share)));

            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            GomsLog.d(TAG, e.toString());
        } finally {
            mLltTarotSpread.destroyDrawingCache();
            //mLltTarotSpread.setVisibility(View.GONE);
            mLltTarotSpread.setBackground(null);
        }
    }

    private void goSave(){

    }

    private void goConsulting(){

    }

    @Override
    public void onBackPressed()
    {
        if (mBackPress)
            super.onBackPressed();
        
    }

    public GomsRequestDialog setTitle(String title)
    {
        mTitle.setText(title);
        return this;
    }
    
    public GomsRequestDialog setOKMsg(String str)
    {
        mOK.setText(str);
        return this;
    }
    
    public GomsRequestDialog setBackPress(boolean boo)
    {
        mBackPress = boo;
        return this;
    }
    
    @Override
    public void onClick(View v)
    {
        GomsLog.d(TAG, "onClick : " + v.getId());
        GomsLog.d(TAG, "onClick : " + R.id.btn_share);

        int i = v.getId();
        if (i == R.id.dialog_common_ok)
        {
            dismiss();
            if (mOkCallback != null)
                mOkCallback.onClickOk("");
        }else if(i == R.id.ib_reveal){
            goReveal();
        }else if(i == R.id.btn_share){
            goShare();
        }else if(i == R.id.btn_save){
            goSave();
        }else if(i == R.id.btn_consulting){
            goConsulting();
        }
    }
}
