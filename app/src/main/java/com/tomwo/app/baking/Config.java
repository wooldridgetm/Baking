package com.tomwo.app.baking;

/**
 * Created by wooldridgetm on 9/16/17.
 */

public final class Config
{
    public static final String LINESPACE = "																																            .";

    // TODO: make this assignable via RemoteConfig
    public static final String RECIPE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";



    // used in MainActivity & RecipeChooserActivity
    public static final String TAG_MODEL = "model";
    public static final String TAG_MAIN  = "mainList";

    // pref key to get the Recipe for a specific AppWidgetID
    public static final String PREF_RECIPE_FOR_APPWIDGET_ID   = "com.tomwo.app.baking.prefRecipeForAppWidgetId_";

    // arguments to put into the Intent
    public static final String ARG_APPWIDGET_ID  = "com.tomwo.app.baking.appWidgetId";   // single appWidgetId
    public static final String ARG_APPWIDGET_IDS = "com.tomwo.app.baking.appWidgetIds"; // multiple appWidgetIds
    public static final String ARG_RECIPE_ID     = "com.tomwo.app.baking.recipeId";
    public static final String ARG_RECIPE_NAME   = "com.tomwo.app.baking.recipeName";   // name of recipe - used to launch BlankActivity

    // used in ListRemoteViewsFactory
    public static final String ARG_INGREDIENT    = "com.tomwo.app.baking.ingredient";


    // actions in our Intent - COMMAND pattern in MyIntentService
    public static final String ACTION_UPDATE = "com.tomwo.app.baking.ui.widget.updateWidget";
    public static final String ACTION_SAVE_AND_INITIALIZE = "com.tomwo.app.baking.ui.widget.saveRecipeIdAndInitializeWidget"; // when user drags widget onto the screen
}
