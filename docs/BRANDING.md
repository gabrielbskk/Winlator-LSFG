# Aurora experimental identity

**Working name:** Winlator LSFG Aurora

Aurora is a visual identity for [@gabrielbskk](https://github.com/gabrielbskk)'s experimental derivative of the original [Winlator LSFG project maintained by Rafaela (@amyroseee)](https://github.com/amyroseee/Winlator-LSFG). Its technical foundation is [Winlator by brunodev85](https://github.com/brunodev85/winlator) and additional independent upstream software.

## Image assets
- SVG project mark: [assets/branding/aurora-symbol.svg](../assets/branding/aurora-symbol.svg)
- Android adaptive foreground vector: `app/app/src/main/res/drawable/aurora_launcher_foreground.xml`
- Android launcher metadata: `app/app/src/main/res/mipmap-anydpi-v26/ic_launcher*.xml`
- Retained upstream bitmap icons and logos are left untouched for attribution, packaging fallback or unrelated UI references.

## Palette
| Role | Color |
| --- | --- |
| Deep navy | `#061725` |
| Aurora cyan | `#20CFFA` |
| Mint highlight | `#57FFDD` |
| Blue accent | `#286FAE` |
| Light detail | `#C7FFF8` |

## Naming and compatibility
The displayed Android app label becomes `Winlator LSFG Aurora` while the Gradle `applicationId` and Java namespace remain `com.winlator`. Those technical identifiers must **not** be renamed without a separate migration plan addressing side-by-side installation, existing user data, backup/restore and file provider authorities.

The repository slug is unchanged by this PR; GitHub repository renaming is an administrative settings operation, not an Android label or README change.

## Claims and attribution
Do not present mock UI, promotional concept images, generated sample FPS figures or hypothetical GPU performance as validated measurements. Discord RPC remains experimental, and full Windows 32-bit compatibility needs real-game tests after a new build. Display Rafaela's original fork credit prominently and retain all upstream copyright/license notices.

The new abstract Aurora symbol is independently created project artwork with AI assistance; it intentionally avoids official Winlator, Discord and Lossless Scaling trademarks.
