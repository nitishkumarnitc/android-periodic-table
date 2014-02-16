package com.frozendevs.periodictable.model.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.frozendevs.periodictable.R;
import com.frozendevs.periodictable.helper.Database;
import com.frozendevs.periodictable.model.ElementProperties;

public class PropertiesAdapter extends BaseAdapter {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private Context context;
    private Property[] propertiesPairs = new Property[] {  };

    private class Property {
        int name;
        String value;

        Property(int name, String value) {
            this.name = name;
            this.value = value;
        }

        Property(int name, int value) {
            this(name, String.valueOf(value));
        }
    }

    private class LoadProperties extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            ElementProperties properties = Database.getElementProperties(context, params[0]);

            propertiesPairs = new Property[] {
                    new Property(R.string.properties_header_general, null),
                    new Property(R.string.property_symbol, properties.getSymbol()),
                    new Property(R.string.property_atomic_number, properties.getAtomicNumber()),
                    new Property(R.string.property_weight, properties.getStandardAtomicWeight()),
                    new Property(R.string.property_group, properties.getGroup() > 0 ?
                            properties.getGroup() : 3),
                    new Property(R.string.property_period, properties.getPeriod()),
                    new Property(R.string.property_block, properties.getBlock()),
                    new Property(R.string.property_category, properties.getCategory()),
                    new Property(R.string.property_electron_configuration,
                            properties.getElectronConfiguration()),
                    new Property(R.string.properties_header_physical, null),
                    new Property(R.string.property_appearance, properties.getAppearance()),
                    new Property(R.string.property_phase, properties.getPhase()),
                    new Property(R.string.property_density, properties.getDensity()),
                    new Property(R.string.property_liquid_density_at_mp,
                            properties.getLiquidDensityAtMeltingPoint()),
                    new Property(R.string.property_liquid_density_at_bp,
                            properties.getLiquidDensityAtBoilingPoint()),
                    new Property(R.string.property_melting_point, properties.getMeltingPoint()),
                    new Property(R.string.property_boiling_point, properties.getBoilingPoint()),
                    new Property(R.string.property_triple_point, properties.getTriplePoint()),
                    new Property(R.string.property_critical_point, properties.getCriticalPoint()),
                    new Property(R.string.property_heat_of_fusion, properties.getHeatOfFusion()),
                    new Property(R.string.property_heat_of_vaporization,
                            properties.getHeatOfVaporization())
            };

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            notifyDataSetChanged();
        }
    }

    public PropertiesAdapter(Context context, int atomicNumber) {
        this.context = context;

        new LoadProperties().execute(atomicNumber);
    }

    @Override
    public int getCount() {
        return propertiesPairs.length;
    }

    @Override
    public Property getItem(int position) {
        return propertiesPairs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(getItemViewType(position) == VIEW_TYPE_ITEM
                ? R.layout.properties_list_item : R.layout.properties_list_header, parent, false);

        Property property = getItem(position);

        if(getItemViewType(position) == VIEW_TYPE_ITEM) {
            ((TextView)view.findViewById(R.id.property_name)).setText(property.name);
            ((TextView)view.findViewById(R.id.property_value)).setText(!property.value.equals("") ?
                    property.value : context.getString(R.string.property_value_unknown));
        }
        else {
            ((TextView)view).setText(property.name);
        }

        return view;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return getItemViewType(position) != VIEW_TYPE_HEADER;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).value != null ? VIEW_TYPE_ITEM : VIEW_TYPE_HEADER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
}
