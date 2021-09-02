package org.fog_rock.photo_slideshow.core.math

interface SizeCalculator {

    /**
     * 画像の元サイズから期待されるサイズに近づけるための、最も効率的な縮小率を求める.
     * Ex. org(1080,1920), exp(500,1000) の場合、最も期待サイズに近い値は (540,960) なので 0.5 を返す.
     */
    fun estimateEffectiveScale(orgWidth: Long, orgHeight: Long, expWidth: Long, expHeight: Long): Float
}