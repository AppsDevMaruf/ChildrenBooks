package com.childrenbooks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.childrenbooks.data.local.LearningCatalog;
import com.childrenbooks.domain.model.AlphabetLesson;
import com.childrenbooks.domain.model.NumberLesson;
import com.childrenbooks.domain.model.PictureLesson;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/** Regression checks for the local learning content shipped in the app. */
public class ExampleUnitTest {
    @Test
    public void alphabetContent_isCompleteAndPlayable() {
        List<AlphabetLesson> alphabet = LearningCatalog.INSTANCE.getAlphabet();

        assertEquals(26, alphabet.size());
        assertEquals("A", alphabet.get(0).getLetter());
        assertEquals("Z", alphabet.get(alphabet.size() - 1).getLetter());
        for (AlphabetLesson lesson : alphabet) {
            assertFalse(lesson.getLetter().trim().isEmpty());
            assertTrue(lesson.getCapitalImage() != 0);
            assertTrue(lesson.getExampleImage() != 0);
            assertTrue(lesson.getSmallImage() != 0);
            assertTrue(lesson.getLetterSound() != 0);
            assertTrue(lesson.getWordSound() != 0);
        }
    }

    @Test
    public void numberContent_isCompleteAndPlayable() {
        List<NumberLesson> numbers = LearningCatalog.INSTANCE.getNumbers();

        assertEquals(21, numbers.size());
        assertEquals("0", numbers.get(0).getNumber());
        assertEquals("20", numbers.get(numbers.size() - 1).getNumber());
        for (NumberLesson lesson : numbers) {
            assertTrue(lesson.getNumberImage() != 0);
            assertTrue(lesson.getCountImage() != 0);
            assertTrue(lesson.getNumberSound() != 0);
            assertTrue(lesson.getWordSound() != 0);
        }
    }

    @Test
    public void pictureCategories_haveTwentyLessonsEach() {
        List<List<PictureLesson>> categories = Arrays.asList(
                LearningCatalog.INSTANCE.getAnimals(),
                LearningCatalog.INSTANCE.getFoods(),
                LearningCatalog.INSTANCE.getFlowers(),
                LearningCatalog.INSTANCE.getBirds(),
                LearningCatalog.INSTANCE.getFurniture(),
                LearningCatalog.INSTANCE.getFruits(),
                LearningCatalog.INSTANCE.getVegetables(),
                LearningCatalog.INSTANCE.getShapes()
        );

        for (List<PictureLesson> category : categories) {
            assertEquals(20, category.size());
            for (PictureLesson lesson : category) {
                assertFalse(lesson.getTitle().trim().isEmpty());
                assertTrue(lesson.getImage() != 0);
            }
        }
    }
}
