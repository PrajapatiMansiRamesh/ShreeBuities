package com.tecmanic.gogrocer.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tecmanic.gogrocer.Adapters.FlagAdapter;
import com.tecmanic.gogrocer.ModelClass.Country;
import com.tecmanic.gogrocer.R;
import com.tecmanic.gogrocer.util.CommunicatorFlag;

import java.util.ArrayList;
import java.util.List;

public class FlagActivity extends AppCompatActivity {

    private ImageView back_btn;
    private RecyclerView flag_count;
    private List<Country> countryList = new ArrayList<>();
    private List<Country> searchCountryList = new ArrayList<>();
    private FlagAdapter adapter;
    private View back_v;
    private ContentLoadingProgressBar progressBar;
    private int flagResource = -1;
    private String countryCode = "";
    private boolean flagSelected = false;
    private EditText txtSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flag);
        back_btn = findViewById(R.id.back_btn);
        flag_count = findViewById(R.id.flag_country);
        progressBar = findViewById(R.id.progress_bar);
        back_v = findViewById(R.id.back_v);
        txtSearch = findViewById(R.id.txtSearch);

        adapter = new FlagAdapter(FlagActivity.this, countryList, searchCountryList, new CommunicatorFlag() {
            @Override
            public void onClick(String dialCode, int flag) {
                flagResource = flag;
                countryCode = dialCode;
                flagSelected = true;
                onBackPressed();
            }
        });


        back_btn.setOnClickListener(view -> {
            flagSelected = false;
            onBackPressed();
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!txtSearch.getText().toString().equalsIgnoreCase("") && txtSearch.getText().length() > 0) {
                    adapter.filterList(txtSearch.getText().toString());
                }
            }
        });

        flag_count.setLayoutManager(new LinearLayoutManager(FlagActivity.this, LinearLayoutManager.VERTICAL, false));
        flag_count.setAdapter(adapter);
        setUpFlag();
    }

    private void setUpFlag() {
        countryList.clear();
        back_v.setVisibility(View.VISIBLE);
        progressBar.show();
//        World.init(getApplicationContext());
        new Thread(() -> {
            List<Country> countryLists = getCountryList();
            countryList.addAll(countryLists);
            searchCountryList.addAll(countryLists);
            adapter.notifyDataSetChanged();

            runOnUiThread(() -> {
                back_v.setVisibility(View.GONE);
                progressBar.hide();
            });
        }).start();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("countryflag", flagResource);
        intent.putExtra("countrycode", countryCode);
        intent.putExtra("flagSelected", flagSelected);
        setResult(15, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }

    public List<Country> getCountryList() {
        List<com.tecmanic.gogrocer.ModelClass.Country> countries = new ArrayList<>();
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AD", "Andorra", "+376", R.drawable.flag_ad));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AE", "United Arab Emirates", "+971", R.drawable.flag_ae));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AF", "Afghanistan", "+93", R.drawable.flag_af));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AG", "Antigua and Barbuda", "+1", R.drawable.flag_ag));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AI", "Anguilla", "+1", R.drawable.flag_ai));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AL", "Albania", "+355", R.drawable.flag_al));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AM", "Armenia", "+374", R.drawable.flag_am));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AO", "Angola", "+244", R.drawable.flag_ao));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AQ", "Antarctica", "+672", R.drawable.flag_aq));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AR", "Argentina", "+54", R.drawable.flag_ar));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AS", "AmericanSamoa", "+1", R.drawable.flag_as));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AT", "Austria", "+43", R.drawable.flag_at));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AU", "Australia", "+61", R.drawable.flag_au));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AW", "Aruba", "+297", R.drawable.flag_aw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AX", "Åland Islands", "+358", R.drawable.flag_ax));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("AZ", "Azerbaijan", "+994", R.drawable.flag_az));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BA", "Bosnia and Herzegovina", "+387", R.drawable.flag_ba));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BB", "Barbados", "+1", R.drawable.flag_bb));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BD", "Bangladesh", "+880", R.drawable.flag_bd));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BE", "Belgium", "+32", R.drawable.flag_be));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BF", "Burkina Faso", "+226", R.drawable.flag_bf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BG", "Bulgaria", "+359", R.drawable.flag_bg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BH", "Bahrain", "+973", R.drawable.flag_bh));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BI", "Burundi", "+257", R.drawable.flag_bi));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BJ", "Benin", "+229", R.drawable.flag_bj));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BL", "Saint Barthélemy", "+590", R.drawable.flag_bl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BM", "Bermuda", "+1", R.drawable.flag_bm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BN", "Brunei Darussalam", "+673", R.drawable.flag_bn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BO", "Bolivia, Plurinational State of", "+591", R.drawable.flag_bo));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BQ", "Bonaire", "+599", R.drawable.flag_bq));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BR", "Brazil", "+55", R.drawable.flag_br));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BS", "Bahamas", "+1", R.drawable.flag_bs));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BT", "Bhutan", "+975", R.drawable.flag_bt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BV", "Bouvet Island", "+47", R.drawable.flag_bv));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BW", "Botswana", "+267", R.drawable.flag_bw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BY", "Belarus", "+375", R.drawable.flag_by));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("BZ", "Belize", "+501", R.drawable.flag_bz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CA", "Canada", "+1", R.drawable.flag_ca));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CC", "Cocos (Keeling) Islands", "+61", R.drawable.flag_cc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CD", "Congo, The Democratic Republic of the", "+243", R.drawable.flag_cd));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CF", "Central African Republic", "+236", R.drawable.flag_cf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CG", "Congo", "+242", R.drawable.flag_cg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CH", "Switzerland", "+41", R.drawable.flag_ch));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CI", "Ivory Coast", "+225", R.drawable.flag_ci));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CK", "Cook Islands", "+682", R.drawable.flag_ck));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CL", "Chile", "+56", R.drawable.flag_cl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CM", "Cameroon", "+237", R.drawable.flag_cm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CN", "China", "+86", R.drawable.flag_cn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CO", "Colombia", "+57", R.drawable.flag_co));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CR", "Costa Rica", "+506", R.drawable.flag_cr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CU", "Cuba", "+53", R.drawable.flag_cu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CV", "Cape Verde", "+238", R.drawable.flag_cv));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CW", "Curacao", "+599", R.drawable.flag_cw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CX", "Christmas Island", "+61", R.drawable.flag_cx));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CY", "Cyprus", "+357", R.drawable.flag_cy));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("CZ", "Czech Republic", "+420", R.drawable.flag_cz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("DE", "Germany", "+49", R.drawable.flag_de));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("DJ", "Djibouti", "+253", R.drawable.flag_dj));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("DK", "Denmark", "+45", R.drawable.flag_dk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("DM", "Dominica", "+1", R.drawable.flag_dm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("DO", "Dominican Republic", "+1", R.drawable.flag_do));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("DZ", "Algeria", "+213", R.drawable.flag_dz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("EC", "Ecuador", "+593", R.drawable.flag_ec));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("EE", "Estonia", "+372", R.drawable.flag_ee));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("EG", "Egypt", "+20", R.drawable.flag_eg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("EH", "Western Sahara", "+212", R.drawable.flag_eh));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ER", "Eritrea", "+291", R.drawable.flag_er));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ES", "Spain", "+34", R.drawable.flag_es));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ET", "Ethiopia", "+251", R.drawable.flag_et));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("FI", "Finland", "+358", R.drawable.flag_fi));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("FJ", "Fiji", "+679", R.drawable.flag_fj));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("FK", "Falkland Islands (Malvinas)", "+500", R.drawable.flag_fk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("FM", "Micronesia, Federated States of", "+691", R.drawable.flag_fm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("FO", "Faroe Islands", "+298", R.drawable.flag_fo));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("FR", "France", "+33", R.drawable.flag_fr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GA", "Gabon", "+241", R.drawable.flag_ga));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GB", "United Kingdom", "+44", R.drawable.flag_gb));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GD", "Grenada", "+1", R.drawable.flag_gd));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GE", "Georgia", "+995", R.drawable.flag_ge));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GF", "French Guiana", "+594", R.drawable.flag_gf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GG", "Guernsey", "+44", R.drawable.flag_gg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GH", "Ghana", "+233", R.drawable.flag_gh));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GI", "Gibraltar", "+350", R.drawable.flag_gi));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GL", "Greenland", "+299", R.drawable.flag_gl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GM", "Gambia", "+220", R.drawable.flag_gm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GN", "Guinea", "+224", R.drawable.flag_gn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GP", "Guadeloupe", "+590", R.drawable.flag_gp));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GQ", "Equatorial Guinea", "+240", R.drawable.flag_gq));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GR", "Greece", "+30", R.drawable.flag_gr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GS", "South Georgia and the South Sandwich Islands", "+500", R.drawable.flag_gs));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GT", "Guatemala", "+502", R.drawable.flag_gt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GU", "Guam", "+1", R.drawable.flag_gu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GW", "Guinea-Bissau", "+245", R.drawable.flag_gw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("GY", "Guyana", "+595", R.drawable.flag_gy));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("HK", "Hong Kong", "+852", R.drawable.flag_hk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("HM", "Heard Island and McDonald Islands", "", R.drawable.flag_hm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("HN", "Honduras", "+504", R.drawable.flag_hn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("HR", "Croatia", "+385", R.drawable.flag_hr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("HT", "Haiti", "+509", R.drawable.flag_ht));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("HU", "Hungary", "+36", R.drawable.flag_hu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ID", "Indonesia", "+62", R.drawable.flag_id));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IE", "Ireland", "+353", R.drawable.flag_ie));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IL", "Israel", "+972", R.drawable.flag_il));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IM", "Isle of Man", "+44", R.drawable.flag_im));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IN", "India", "+91", R.drawable.flag_in));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IO", "British Indian Ocean Territory", "+246", R.drawable.flag_io));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IQ", "Iraq", "+964", R.drawable.flag_iq));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IR", "Iran, Islamic Republic of", "+98", R.drawable.flag_ir));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IS", "Iceland", "+354", R.drawable.flag_is));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("IT", "Italy", "+39", R.drawable.flag_it));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("JE", "Jersey", "+44", R.drawable.flag_je));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("JM", "Jamaica", "+1", R.drawable.flag_jm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("JO", "Jordan", "+962", R.drawable.flag_jo));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("JP", "Japan", "+81", R.drawable.flag_jp));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KE", "Kenya", "+254", R.drawable.flag_ke));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KG", "Kyrgyzstan", "+996", R.drawable.flag_kg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KH", "Cambodia", "+855", R.drawable.flag_kh));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KI", "Kiribati", "+686", R.drawable.flag_ki));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KM", "Comoros", "+269", R.drawable.flag_km));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KN", "Saint Kitts and Nevis", "+1", R.drawable.flag_kn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KP", "North Korea", "+850", R.drawable.flag_kp));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KR", "South Korea", "+82", R.drawable.flag_kr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KW", "Kuwait", "+965", R.drawable.flag_kw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KY", "Cayman Islands", "+345", R.drawable.flag_ky));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("KZ", "Kazakhstan", "+7", R.drawable.flag_kz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LA", "Lao People's Democratic Republic", "+856", R.drawable.flag_la));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LB", "Lebanon", "+961", R.drawable.flag_lb));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LC", "Saint Lucia", "+1", R.drawable.flag_lc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LI", "Liechtenstein", "+423", R.drawable.flag_li));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LK", "Sri Lanka", "+94", R.drawable.flag_lk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LR", "Liberia", "+231", R.drawable.flag_lr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LS", "Lesotho", "+266", R.drawable.flag_ls));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LT", "Lithuania", "+370", R.drawable.flag_lt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LU", "Luxembourg", "+352", R.drawable.flag_lu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LV", "Latvia", "+371", R.drawable.flag_lv));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("LY", "Libyan Arab Jamahiriya", "+218", R.drawable.flag_ly));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MA", "Morocco", "+212", R.drawable.flag_ma));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MC", "Monaco", "+377", R.drawable.flag_mc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MD", "Moldova, Republic of", "+373", R.drawable.flag_md));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ME", "Montenegro", "+382", R.drawable.flag_me));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MF", "Saint Martin", "+590", R.drawable.flag_mf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MG", "Madagascar", "+261", R.drawable.flag_mg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MH", "Marshall Islands", "+692", R.drawable.flag_mh));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MK", "Macedonia, The Former Yugoslav Republic of", "+389", R.drawable.flag_mk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ML", "Mali", "+223", R.drawable.flag_ml));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MM", "Myanmar", "+95", R.drawable.flag_mm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MN", "Mongolia", "+976", R.drawable.flag_mn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MO", "Macao", "+853", R.drawable.flag_mo));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MP", "Northern Mariana Islands", "+1", R.drawable.flag_mp));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MQ", "Martinique", "+596", R.drawable.flag_mq));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MR", "Mauritania", "+222", R.drawable.flag_mr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MS", "Montserrat", "+1", R.drawable.flag_ms));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MT", "Malta", "+356", R.drawable.flag_mt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MU", "Mauritius", "+230", R.drawable.flag_mu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MV", "Maldives", "+960", R.drawable.flag_mv));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MW", "Malawi", "+265", R.drawable.flag_mw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MX", "Mexico", "+52", R.drawable.flag_mx));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MY", "Malaysia", "+60", R.drawable.flag_my));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("MZ", "Mozambique", "+258", R.drawable.flag_mz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NA", "Namibia", "+264", R.drawable.flag_na));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NC", "New Caledonia", "+687", R.drawable.flag_nc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NE", "Niger", "+227", R.drawable.flag_ne));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NF", "Norfolk Island", "+672", R.drawable.flag_nf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NG", "Nigeria", "+234", R.drawable.flag_ng));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NI", "Nicaragua", "+505", R.drawable.flag_ni));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NL", "Netherlands", "+31", R.drawable.flag_nl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NO", "Norway", "+47", R.drawable.flag_no));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NP", "Nepal", "+977", R.drawable.flag_np));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NR", "Nauru", "+674", R.drawable.flag_nr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NU", "Niue", "+683", R.drawable.flag_nu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("NZ", "New Zealand", "+64", R.drawable.flag_nz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("OM", "Oman", "+968", R.drawable.flag_om));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PA", "Panama", "+507", R.drawable.flag_pa));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PE", "Peru", "+51", R.drawable.flag_pe));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PF", "French Polynesia", "+689", R.drawable.flag_pf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PG", "Papua New Guinea", "+675", R.drawable.flag_pg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PH", "Philippines", "+63", R.drawable.flag_ph));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PK", "Pakistan", "+92", R.drawable.flag_pk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PL", "Poland", "+48", R.drawable.flag_pl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PM", "Saint Pierre and Miquelon", "+508", R.drawable.flag_pm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PN", "Pitcairn", "+872", R.drawable.flag_pn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PR", "Puerto Rico", "+1", R.drawable.flag_pr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PS", "Palestinian Territory, Occupied", "+970", R.drawable.flag_ps));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PT", "Portugal", "+351", R.drawable.flag_pt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PW", "Palau", "+680", R.drawable.flag_pw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("PY", "Paraguay", "+595", R.drawable.flag_py));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("QA", "Qatar", "+974", R.drawable.flag_qa));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("RE", "Réunion", "+262", R.drawable.flag_re));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("RO", "Romania", "+40", R.drawable.flag_ro));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("RS", "Serbia", "+381", R.drawable.flag_rs));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("RU", "Russia", "+7", R.drawable.flag_ru));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("RW", "Rwanda", "+250", R.drawable.flag_rw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SA", "Saudi Arabia", "+966", R.drawable.flag_sa));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SB", "Solomon Islands", "+677", R.drawable.flag_sb));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SC", "Seychelles", "+248", R.drawable.flag_sc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SD", "Sudan", "+249", R.drawable.flag_sd));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SE", "Sweden", "+46", R.drawable.flag_se));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SG", "Singapore", "+65", R.drawable.flag_sg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SH", "Saint Helena, Ascension and Tristan Da Cunha", "+290", R.drawable.flag_sh));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SI", "Slovenia", "+386", R.drawable.flag_si));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SJ", "Svalbard and Jan Mayen", "+47", R.drawable.flag_sj));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SK", "Slovakia", "+421", R.drawable.flag_sk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SL", "Sierra Leone", "+232", R.drawable.flag_sl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SM", "San Marino", "+378", R.drawable.flag_sm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SN", "Senegal", "+221", R.drawable.flag_sn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SO", "Somalia", "+252", R.drawable.flag_so));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SR", "Suriname", "+597", R.drawable.flag_sr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SS", "South Sudan", "+211", R.drawable.flag_ss));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ST", "Sao Tome and Principe", "+239", R.drawable.flag_st));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SV", "El Salvador", "+503", R.drawable.flag_sv));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SX", "  Sint Maarten", "+1", R.drawable.flag_sx));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SY", "Syrian Arab Republic", "+963", R.drawable.flag_sy));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("SZ", "Swaziland", "+268", R.drawable.flag_sz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TC", "Turks and Caicos Islands", "+1", R.drawable.flag_tc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TD", "Chad", "+235", R.drawable.flag_td));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TF", "French Southern Territories", "+262", R.drawable.flag_tf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TG", "Togo", "+228", R.drawable.flag_tg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TH", "Thailand", "+66", R.drawable.flag_th));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TJ", "Tajikistan", "+992", R.drawable.flag_tj));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TK", "Tokelau", "+690", R.drawable.flag_tk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TL", "East Timor", "+670", R.drawable.flag_tl));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TM", "Turkmenistan", "+993", R.drawable.flag_tm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TN", "Tunisia", "+216", R.drawable.flag_tn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TO", "Tonga", "+676", R.drawable.flag_to));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TR", "Turkey", "+90", R.drawable.flag_tr));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TT", "Trinidad and Tobago", "+1", R.drawable.flag_tt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TV", "Tuvalu", "+688", R.drawable.flag_tv));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TW", "Taiwan", "+886", R.drawable.flag_tw));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("TZ", "Tanzania, United Republic of", "+255", R.drawable.flag_tz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("UA", "Ukraine", "+380", R.drawable.flag_ua));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("UG", "Uganda", "+256", R.drawable.flag_ug));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("UM", "U.S. Minor Outlying Islands", "", R.drawable.flag_um));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("US", "United States", "+1", R.drawable.flag_us));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("UY", "Uruguay", "+598", R.drawable.flag_uy));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("UZ", "Uzbekistan", "+998", R.drawable.flag_uz));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VA", "Holy See (Vatican City State)", "+379", R.drawable.flag_va));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VC", "Saint Vincent and the Grenadines", "+1", R.drawable.flag_vc));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VE", "Venezuela, Bolivarian Republic of", "+58", R.drawable.flag_ve));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VG", "Virgin Islands, British", "+1", R.drawable.flag_vg));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VI", "Virgin Islands, U.S.", "+1", R.drawable.flag_vi));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VN", "Viet Nam", "+84", R.drawable.flag_vn));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("VU", "Vanuatu", "+678", R.drawable.flag_vu));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("WF", "Wallis and Futuna", "+681", R.drawable.flag_wf));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("WS", "Samoa", "+685", R.drawable.flag_ws));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("XK", "Kosovo", "+383", R.drawable.flag_xk));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("YE", "Yemen", "+967", R.drawable.flag_ye));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("YT", "Mayotte", "+262", R.drawable.flag_yt));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ZA", "South Africa", "+27", R.drawable.flag_za));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ZM", "Zambia", "+260", R.drawable.flag_zm));
        countries.add(new com.tecmanic.gogrocer.ModelClass.Country("ZW", "Zimbabwe", "+263", R.drawable.flag_zw));


        return countries;
    }
}