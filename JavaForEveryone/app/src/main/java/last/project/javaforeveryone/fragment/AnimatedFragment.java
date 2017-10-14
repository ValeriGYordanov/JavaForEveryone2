package last.project.javaforeveryone.fragment;

import android.support.v4.app.Fragment;
import android.view.animation.Animation;

import com.labo.kaji.fragmentanimations.CubeAnimation;

public abstract class AnimatedFragment extends Fragment {

    /**
     * Method required by the labo.kaji.fragmentanimations
     *
     * @param transit - type of animation, can be chosen from the library
     * @param enter - If the fragment enters or exits
     * @param nextAnim - The time between transitions
     * @return
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return CubeAnimation.create(CubeAnimation.UP, enter,700);
    }
}
