package com.example.android.stockkeepingassistant.view.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.model.Product;
import com.example.android.stockkeepingassistant.model.Warehouse;

import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("FieldCanBeLocal")
public class ProductFragment extends Fragment implements TextWatcher, View.OnClickListener{
    /* Class variables */
    private ImageView productImage;
    private ImageButton productCamera;
    private EditText productTitle;
    private EditText productQuantity;
    private EditText productPrice;
    private Spinner supplierName;
    private TextView supplierEmail;
    private Button increaseQuantity;
    private Button decreaseQuantity;
    private Product product;
    private Warehouse warehouse;

    private static final String ARG_ID = "product_id";

    public static ProductFragment newInstance(UUID productId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, productId);

        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            UUID productId = Objects.requireNonNull((UUID) getArguments().getSerializable(ARG_ID));
            warehouse = Warehouse.getInstance(getActivity());
            product = warehouse.getProduct(productId);
        } else {
            throw new IllegalStateException("Fragment cannot be instantiated w/o product ID");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        productImage = view.findViewById(R.id.editor_product_image);
        productCamera = view.findViewById(R.id.editor_product_camera);

        productTitle = view.findViewById(R.id.editor_product_title);
        productTitle.setText(product.getTitle());
        productTitle.addTextChangedListener(this);

        productQuantity = view.findViewById(R.id.editor_product_quantity);
        productQuantity.setText(String.valueOf(product.getQuantity()));
        productQuantity.addTextChangedListener(this);

        increaseQuantity = view.findViewById(R.id.editor_quantity_increment);
        increaseQuantity.setOnClickListener(this);
        decreaseQuantity = view.findViewById(R.id.editor_quantity_decrement);
        decreaseQuantity.setOnClickListener(this);

        productPrice = view.findViewById(R.id.editor_product_price);
        productPrice.addTextChangedListener(this);

        supplierName = view.findViewById(R.id.spinner_supplier_name);
        setupSpinner();

        supplierEmail = view.findViewById(R.id.editor_supplier_email);

        return view;
    }

    private void setupWatchersOnInputFields() {
        // TODO: 4/10/18 Set TextWatchers on all EditText child views
    }

    private void setupSpinner() {
        Context context = Objects.requireNonNull(getActivity());
        // Create adapter for spinner from the String array
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.array_supplier_contact_option, android.R.layout.simple_spinner_item);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        supplierName.setAdapter(supplierSpinnerAdapter);

        // Get user selected supplier and map to app email ID
        Warehouse warehouse = Warehouse.getInstance(context);
        supplierName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    product.setSupplierName(selection);
                    product.setSupplierEmail(warehouse.resolveEmail(selection));
                    supplierEmail.setText(product.getSupplierEmail());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String firstInList = (String) parent.getItemAtPosition(0);
                product.setSupplierEmail(warehouse.resolveEmail(firstInList)); // Default is 1st in list
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        /* no-op */
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        updateProduct();
    }

    private void updateProduct() {
        // TODO: 4/9/18 Add implementation
    }

    @Override
    public void afterTextChanged(Editable s) {
        /* no-op */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editor_quantity_increment:
                int units = Integer.parseInt(productQuantity.getText().toString().trim());
                productQuantity.setText(String.valueOf(++units));
                break;
            case R.id.editor_quantity_decrement:
                int quantity = Integer.parseInt(productQuantity.getText().toString().trim());
                if (quantity > 0) {
                    productQuantity.setText(String.valueOf(--quantity));
                } else {
                    // The product is currently not in stock and cannot be decreased.
                    Toast.makeText(
                            getActivity(),
                            getString(R.string.editor_decrement_button_negative),
                            Toast.LENGTH_SHORT
                    ).show();
                }
                break;
        }
    }
}
