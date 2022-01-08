package com.example.arthurvanremoortel_werkstuk.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import java.io.File

@Database(entities = [Recipe::class, Ingredient::class, PreparationStep::class], version = 2, exportSchema = false)
// Source: https://developer.android.com/codelabs/android-room-with-a-view-kotlin
public abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun preparationStepDao(): PreparationStepDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
//                    scope.launch {
//                        populateDatabase(database)
//                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase, user: FirebaseUser) {
            // Delete all content here.
            val recipeDao = database.recipeDao()
            val ingredientDao = database.ingredientDao()
            val preparationStepDao = database.preparationStepDao()
            recipeDao.deleteAll()
            ingredientDao.deleteAll()
            preparationStepDao.deleteAll()

            val recipe1 = Recipe(1,"Pizza Margherita", 8.0, 20, null, user.email)
            recipeDao.insert(recipe1)
            var rid: Long = recipe1.recipeId!!
            val ingredients1 = listOf<Ingredient>(
                Ingredient(null, rid, "unbleached all-purpose flour", "300 g"),
                Ingredient(null, rid, "granulated sugar", "1 teaspoon"),
                Ingredient(null, rid, "active dry yeast", "1/2 teaspoon"),
                Ingredient(null, rid, "warm water", "7 ounces"),
                Ingredient(null, rid, "extra virgin olive oil", "1 tablespoon"),
                Ingredient(null, rid, "semolina and all-purpose flour ", "for dusting the pizza peel"),
                Ingredient(null, rid, "pureed or crushed canned San Marzano tomatoes", "1 cup"),
                Ingredient(null, rid, "fresh garlic cloves", "2-3"),
                Ingredient(null, rid, "extra virgin olive oil", "1 teaspoon"),
                Ingredient(null, rid, "large pinches of kosher salt to taste", "2-3"),
                Ingredient(null, rid, "freshly ground black pepper", "1/4 teaspoon"),
                Ingredient(null, rid, "7 ounces", "2-3 tablespoons"),
                Ingredient(null, rid, "large fresh basil leaves", "7 ounces"),
                Ingredient(null, rid, "dried red pepper flakes", "5-6"),
            )
            val preparationSteps = listOf<PreparationStep>(
                PreparationStep(null, rid, "Prepare Pizza Dough: In a medium bowl, whisk together the all-purpose flour, sugar, yeast and salt. Add the warm water and olive oil, and stir the mixture with a wooden spoon until the dough just begins to come together. It will seem shaggy and dry, but don’t worry.", null),
                PreparationStep(null, rid, "Scrape the dough onto a well-floured counter top and knead the dough for three minutes. It should quickly come together and begin to get sticky. Dust the dough with flour as needed (sometimes I will have to do this 2 to 3 times, depending on humidity levels) – it should be slightly tacky, but should not be sticking to your counter top.  After about 3 minutes, the dough should be smooth, slightly elastic, and tacky. Lightly grease a large mixing bowl with olive oil, and place the dough into the bowl.", 3),
                PreparationStep(null, rid, "Cover the bowl with a kitchen towel (or plastic wrap) and allow the dough to rise in a warm, dry area of your kitchen for 2 hours or until the dough has doubled in size. Proofing Tip: If your kitchen is very cold, heat a large heatproof measuring cup of water in the microwave for 2 to 3 minutes. This creates a nice warm environment. Remove the cup and place the bowl with the dough in the microwave until it has risen. [If you are preparing the dough in advance, see the note section for freezing instructions.]", 120),
                PreparationStep(null, rid, "Preheat Oven and Pizza Steel or Stone: Place the pizza steel (or stone) on the second to top rack of your oven (roughly 8 inches from the broiler element), and preheat the oven and steel (or stone) to 550°F (285°C) for a minimum of 1 hour. If your oven does not go up to 550°F (285°C) or you are using a delicate pizza stone, I recommend heating it to a maximum of 500°F (260°C)", null),
                PreparationStep(null, rid, "As the oven is preheating, assemble the ingredients. In a small bowl, stir together the pureed tomatoes, minced garlic, extra virgin olive oil, pepper, and salt. Set aside another small bowl with the cubed mozzarella cheese (pat the cheese with a paper towel to remove any excess moisture). Set aside the basil leaves and grated parmigiano-reggiano cheese for easy grabbing.", null),
                PreparationStep(null, rid, "Separate the dough into two equal-sized portions. It will deflate slightly, but that is OK. Place the dough on a large plate or floured counter top, cover gently with plastic wrap, and allow the dough to rest for 5 to 10 minutes.", 10),
                PreparationStep(null, rid, "Assemble the Pizza: Sprinkle the pizza peel (if you do not own a pizza peel, you can try using the back of a half sheet pan - but it is tricky!) with a tablespoon of semolina and dusting of all-purpose flour. Gently use both hands to stretch one ball of pizza dough into roughly a 10-inch circle (don’t worry if its not perfectly uniform). If the dough springs back or is too elastic, allow it to rest for an additional five minutes. The edges of the dough can be slightly thicker, but make sure the center of the dough is thin (you should be able to see some light through it if you held it up). Gently transfer the dough onto the semolina and flour dusted pizza peel or baking sheet.", null),
                PreparationStep(null, rid, "Drizzle or brush the dough lightly (using your fingertips) with olive oil (roughly a teaspoon. Using a large spoon, add roughly ½ cup of the tomato sauce onto the pizza dough, leaving a ½-inch or ¾-inch border on all sides. Use the back of the spoon to spread it evenly and thinly. Sprinkle a tablespoon of parmigiano-reggiano cheese onto the pizza sauce. Add half of the cubed mozzarella, distributing it evenly over the entire pizza. Using your hands, tear a few large basil leaves, and sprinkle the basil over the pizza. At this point, I’ll occasionally stretch the sides of the dough out a bit to make it even thinner. Gently slide the pizza from the peel onto the heated baking stone. Bake for 7 to 8 minutes, or until the crust is golden and the cheese is bubbling and caramelized and the edges of the pizza are golden brown. Note: If you're looking for more color, finish the pizza under the low or medium broil setting, but watch it carefully!\n" +
                        "Remove the pizza carefully from the oven with the pizza peel, transfer to a wooden cutting board or foil, drizzle the top with olive oil, some grated parmigiano-reggiano cheese, and chiffonade of fresh basil. Slice and serve immediately and/or prepare the second pizza.", null),
                PreparationStep(null, rid, "Serving Tip: If you’re serving two pizzas at once, I recommend placing the cooked pizza on a separate baking sheet while you prepare the other pizza. In the last few minutes of cooking, place the prepared pizza into the oven (on a rack below the pizza stone) so that it is extra hot for serving. Otherwise, I recommend serving one pizza fresh out of the oven, keeping the oven hot, and preparing the second pizza after people have gone through the first one! The pizza will taste great either way, but it is at its prime within minutes out of the oven!.", null),
            )
            for (prepStep in preparationSteps) {
                preparationStepDao.insert(prepStep)
            }
            for (ingredient in ingredients1) {
                ingredientDao.insert(ingredient)
            }

            val recipe2 = Recipe(2,"Pizza Hawaii", 3.0, 20, null, user.email)
            rid = recipe2.recipeId!!
            for (prepStep in preparationSteps) {
                preparationStepDao.insert(PreparationStep(null, rid, prepStep.description, prepStep.duration_minutes))
            }
            for (ingredient in ingredients1) {
                ingredientDao.insert(Ingredient(null, rid, ingredient.name, ingredient.amount))
            }
            recipeDao.insert(recipe2)

        }

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

    }

}
