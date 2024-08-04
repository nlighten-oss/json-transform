import BigNumber from "bignumber.js";

export const BigDecimal = BigNumber.clone({
  // We try to fit into Decimal128 which supports 34 decimal digits of precision
  // so in principle, we allow 19 digits for the whole number and 15 for the fraction
  ROUNDING_MODE: BigNumber.ROUND_HALF_UP,
  DECIMAL_PLACES: 15
});

export const RoundingModes: Record<string, number> = {
  UP: BigNumber.ROUND_UP,
  DOWN: BigNumber.ROUND_DOWN,
  CEILING: BigNumber.ROUND_CEIL,
  FLOOR: BigNumber.ROUND_FLOOR,
  HALF_UP: BigNumber.ROUND_HALF_UP,
  HALF_DOWN: BigNumber.ROUND_HALF_DOWN,
  HALF_EVEN: BigNumber.ROUND_HALF_EVEN
}

export const NO_SCALE = -1;
// We try to fit into Decimal128 which supports 34 decimal digits of precision
// so in principle, we allow 19 digits for the whole number and 15 for the fraction
export const MAX_SCALE = 15;
export const MAX_SCALE_ROUNDING = BigNumber.ROUND_HALF_UP;