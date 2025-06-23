package com.example.othellogame.ui.styles

import androidx.compose.ui.graphics.Color
import com.example.othellogame.models.*

object GameStyles {
    fun getStyle(style: GameStyle): GameStyleData = when (style) {
        GameStyle.NORMAL          -> normal
        GameStyle.FUTURISTIC      -> futuristic
        GameStyle.JAPANESE_MODERN -> japaneseModern
    }

    private val NormalAccent   = Color(0xFF26A65B)   
    private val FutureAccent   = Color(0xFFEAEAF0)  
    private val JapaneseAccent = Color(0xFF8B4513)   

    //ノーマル 
    private val normal = GameStyleData(
        boardStyle = BoardStyle(
            backgroundColor = Color(0xFF065535),
            gridColor       = Color.Black,
            textColor       = Color.White,
            buttonColor     = NormalAccent,
            buttonTextColor = Color.White
        ),
        discStyle = DiscStyle(
            blackColor = Color.Black,
            whiteColor = Color.White,
            shape      = DiscShape.CIRCLE
        )
    )

    //フィーチャリング
    private val futuristic = GameStyleData(
        boardStyle = BoardStyle(
            backgroundColor = Color(0xFF1C1C1E),
            gridColor       = FutureAccent,
            gridGlow        = true,              
            gridGlowColor   = FutureAccent,       
            textColor       = FutureAccent,
            buttonColor     = FutureAccent,
            buttonTextColor = Color.Black,
            validMoveColor  = Color.White.copy(alpha = 0.5f)
        ),
        discStyle = DiscStyle(
            blackColor      = Color(0xFF333333),
            whiteColor      = Color.White,
            blackRingColor  = FutureAccent,
            whiteRingColor  = FutureAccent,
            shape           = DiscShape.CIRCLE
        )
    )

    //和モダン 
    private val japaneseModern = GameStyleData(
        boardStyle = BoardStyle(
            backgroundColor = Color(0xFF5D4037),   
            gridColor       = Color(0xFFFFECB3),  
            gridGlow        = true,                
            gridGlowColor   = JapaneseAccent,     
            textColor       = Color(0xFFFFECB3),   
            buttonColor     = Color(0xFFFFECB3),   
            buttonTextColor = Color(0xFF5D4037)  
        ),
        discStyle = DiscStyle(
            blackColor      = Color(0xFFFFECB3),   
            whiteColor      = Color.White,
            blackRingColor  = JapaneseAccent,
            whiteRingColor  = JapaneseAccent,
            shape           = DiscShape.HEXAGON     
        )
    )
} 