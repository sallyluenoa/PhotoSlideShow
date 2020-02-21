package org.fog_rock.photo_slideshow.app.splash.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.fog_rock.photo_slideshow.R

class SignInFragment : Fragment() {

    companion object {

        fun newInstance(): Fragment = SignInFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }
}
