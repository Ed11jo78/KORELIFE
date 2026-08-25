import React from 'react';
import { View } from 'react-native';
import Svg, { Path, Rect, Circle, Polygon, G } from 'react-native-svg';
import { RateProvider } from '../../src/types';

interface BrandLogoProps {
  provider: RateProvider | 'binance' | 'airtm' | 'bcv_usd' | 'bcv_eur' | 'ves' | 'usd' | 'eur';
  size?: number;
}

export default function BrandLogo({ provider, size = 28 }: BrandLogoProps) {
  switch (provider) {
    case 'binance':
      // Official Binance Diamond / Hexagon Logo
      return (
        <View 
          style={{ 
            width: size, 
            height: size, 
            borderRadius: size * 0.32, 
            backgroundColor: '#F3BA2F', 
            alignItems: 'center', 
            justifyContent: 'center',
            overflow: 'hidden'
          }}
        >
          <Svg width={size * 0.72} height={size * 0.72} viewBox="0 0 126.6 126.6">
            {/* Center diamond */}
            <Polygon points="63.3,47.2 47.2,63.3 63.3,79.4 79.4,63.3" fill="#181A20" />
            {/* Top diamond */}
            <Polygon points="63.3,16.2 47.2,32.3 55.3,40.4 63.3,32.4 71.3,40.4 79.4,32.3" fill="#181A20" />
            {/* Bottom diamond */}
            <Polygon points="63.3,110.4 47.2,94.3 55.3,86.2 63.3,94.2 71.3,86.2 79.4,94.3" fill="#181A20" />
            {/* Left diamond */}
            <Polygon points="16.2,63.3 32.3,47.2 40.4,55.3 32.4,63.3 40.4,71.3 32.3,79.4" fill="#181A20" />
            {/* Right diamond */}
            <Polygon points="110.4,63.3 94.3,47.2 86.2,55.3 94.2,63.3 86.2,71.3 94.3,79.4" fill="#181A20" />
          </Svg>
        </View>
      );

    case 'airtm':
      // Official Airtm Logo with Stylized Air Plane / Wings
      return (
        <View 
          style={{ 
            width: size, 
            height: size, 
            borderRadius: size * 0.32, 
            backgroundColor: '#0052FF', 
            alignItems: 'center', 
            justifyContent: 'center',
            overflow: 'hidden'
          }}
        >
          <Svg width={size * 0.68} height={size * 0.68} viewBox="0 0 24 24" fill="none">
            <Path 
              d="M2.5 19.5L12 4.5L21.5 19.5L12 15.5L2.5 19.5Z" 
              fill="#FFFFFF" 
            />
            <Path 
              d="M12 4.5V15.5L21.5 19.5L12 4.5Z" 
              fill="#DCE8FF" 
            />
          </Svg>
        </View>
      );

    case 'bcv_usd':
      // Central Bank building with USD Dollar badge
      return (
        <View 
          style={{ 
            width: size, 
            height: size, 
            borderRadius: size * 0.32, 
            backgroundColor: '#064e3b', 
            alignItems: 'center', 
            justifyContent: 'center',
            borderWidth: 1,
            borderColor: '#10b981'
          }}
        >
          <Svg width={size * 0.65} height={size * 0.65} viewBox="0 0 24 24" fill="none">
            <Path d="M3 21H21" stroke="#34d399" strokeWidth="2" strokeLinecap="round"/>
            <Path d="M3 10H21" stroke="#34d399" strokeWidth="2" strokeLinecap="round"/>
            <Path d="M5 6L12 3L19 6" stroke="#34d399" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <Path d="M4 10V21" stroke="#34d399" strokeWidth="1.8"/>
            <Path d="M20 10V21" stroke="#34d399" strokeWidth="1.8"/>
            <Path d="M8 10V17" stroke="#34d399" strokeWidth="1.8"/>
            <Path d="M12 10V17" stroke="#34d399" strokeWidth="1.8"/>
            <Path d="M16 10V17" stroke="#34d399" strokeWidth="1.8"/>
          </Svg>
        </View>
      );

    case 'bcv_eur':
      // Central Bank building with EUR Euro badge
      return (
        <View 
          style={{ 
            width: size, 
            height: size, 
            borderRadius: size * 0.32, 
            backgroundColor: '#1e1b4b', 
            alignItems: 'center', 
            justifyContent: 'center',
            borderWidth: 1,
            borderColor: '#818cf8'
          }}
        >
          <Svg width={size * 0.65} height={size * 0.65} viewBox="0 0 24 24" fill="none">
            <Path d="M3 21H21" stroke="#a5b4fc" strokeWidth="2" strokeLinecap="round"/>
            <Path d="M3 10H21" stroke="#a5b4fc" strokeWidth="2" strokeLinecap="round"/>
            <Path d="M5 6L12 3L19 6" stroke="#a5b4fc" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            <Path d="M4 10V21" stroke="#a5b4fc" strokeWidth="1.8"/>
            <Path d="M20 10V21" stroke="#a5b4fc" strokeWidth="1.8"/>
            <Path d="M8 10V17" stroke="#a5b4fc" strokeWidth="1.8"/>
            <Path d="M12 10V17" stroke="#a5b4fc" strokeWidth="1.8"/>
            <Path d="M16 10V17" stroke="#a5b4fc" strokeWidth="1.8"/>
          </Svg>
        </View>
      );

    default:
      return null;
  }
}
