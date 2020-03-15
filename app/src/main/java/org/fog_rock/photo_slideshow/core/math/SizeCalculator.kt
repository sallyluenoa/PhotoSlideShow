package org.fog_rock.photo_slideshow.core.math

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class SizeCalculator {

    /**
     * 画像の元サイズから期待されるサイズに近づけるための、最も効率的な縮小率を求める.
     * Ex. org(1080,1920), exp(500,1000) の場合、最も期待サイズに近い値は (540,960) なので 0.5 を返す.
     */
    fun estimateEffectiveScale(
        orgWidth: Long, orgHeight: Long, expWidth: Long, expHeight: Long
    ): Float = estimateEffectiveScaleRecursively(
            min(orgWidth, orgHeight), max(orgWidth, orgHeight),
            min(expWidth, expHeight), max(expWidth, expHeight)
    )

    private fun estimateEffectiveScaleRecursively(
        orgMin: Long, orgMax: Long, expMin: Long, expMax: Long
    ): Float {
        // 期待サイズより元サイズが小さい場合は縮小しない.
        if (orgMin < expMin && orgMax < expMax) return 1.0f

        // 元サイズの半分のサイズで再帰処理をして、最小スケール値を求める.
        val minScale = estimateEffectiveScaleRecursively(orgMin / 2, orgMax / 2, expMin, expMax) * 0.5f

        // 評価値を元に、期待サイズに最も近い効率の良いスケール値を判断する.
        // 縦横それぞれの期待サイズとの差の絶対値をとり、その和を評価値とする.
        // 比較対象: 元サイズ、元サイズに0.75かけたもの、元サイズに最小スケール値(0.5以下)をかけたもの
        val orgEvl = abs(orgMin - expMin) + abs(orgMax - expMax)
        val quaEvl = abs(orgMin * 3 / 4 - expMin) + abs(orgMax * 3 / 4 - expMax)
        val sclEvl = abs((orgMin * minScale).toLong() - expMin) + abs((orgMax * minScale).toLong() - expMax)

        return when( min(orgEvl, min(quaEvl, sclEvl)) ) {
            orgEvl -> 1.0f
            quaEvl -> 0.75f
            else -> minScale
        }
    }
}