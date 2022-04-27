/*
 * Copyright (C) 2020 Beijing Yishu Technology Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.growingio.android.plugin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ColorFragment extends androidx.fragment.app.Fragment {
    private static final String TAG = "OrangeFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private int color;

    public ColorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OrangeFragment.
     */
    public static ColorFragment newInstance(int tag) {
        ColorFragment fragment = new ColorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            color = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_color, container, false);

        TextView tv = root.findViewById(R.id.fragmentTv);
        tv.setText("" + color);
        View bg = root.findViewById(R.id.fragmentBg);
        bg.setBackgroundColor(ContextCompat.getColor(this.getContext(), color));

        Button btn = root.findViewById(R.id.colorBtn);
        btn.setText("" + color);
        btn.setOnClickListener(v -> Log.e(TAG, "btn onClick: "));

        root.setOnClickListener(v -> Log.e(TAG, "root onClick: "));
        return root;
    }

    @Override
    public void onDestroyView() {
        Log.e(TAG, "onDestroyView: ");
        super.onDestroyView();
    }
}