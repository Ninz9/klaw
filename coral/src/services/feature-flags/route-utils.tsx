import { useToast } from "@aivenio/aquarium";
import { useEffect } from "react";
import { Navigate, RouteObject } from "react-router-dom";
import { useAuthContext } from "src/app/context-provider/AuthProvider";
import { Routes } from "src/app/router_utils";
import { Permission } from "src/domain/auth-user/auth-user-types";
import { FeatureFlag } from "src/services/feature-flags/types";
import { isFeatureFlagActive } from "src/services/feature-flags/utils";

type FeatureFlagRouteArgs = {
  path: Routes;
  element: JSX.Element;
  featureFlag: FeatureFlag;
  redirectRouteWithoutFeatureFlag: Routes;
  children?: RouteObject[];
};

type PrivateRouteArgs = {
  path: Routes;
  element: JSX.Element;
  permission: Permission;
  redirectUnauthorized: Routes;
  children?: RouteObject[];
};

function createRouteBehindFeatureFlag({
  path,
  element,
  featureFlag,
  redirectRouteWithoutFeatureFlag,
  children,
}: FeatureFlagRouteArgs) {
  return {
    path: path,
    // if FEATURE_FLAG_<name> is not set to
    // true, we're not even rendering the page but
    // redirect user directly to /topics
    element: isFeatureFlagActive(featureFlag) ? (
      element
    ) : (
      <Navigate to={redirectRouteWithoutFeatureFlag} />
    ),
    ...(children && { children }),
  };
}

const PrivateRoute = ({
  children,
  permission,
  redirectUnauthorized,
}: {
  children: React.ReactNode;
  permission: Permission;
  redirectUnauthorized: Routes;
}) => {
  const { permissions } = useAuthContext();
  const toast = useToast();
  const isAuthorized = permissions[permission];

  useEffect(() => {
    if (!isAuthorized) {
      toast({
        message: `Not authorized`,
        position: "bottom-left",
        variant: "danger",
      });

      return;
    }
  }, []);

  return isAuthorized ? (
    children
  ) : (
    <Navigate to={redirectUnauthorized} replace />
  );
};

function createPrivateRoute({
  path,
  element,
  permission,
  redirectUnauthorized,
  children,
}: PrivateRouteArgs) {
  return {
    path: path,
    element: (
      <PrivateRoute
        permission={permission}
        redirectUnauthorized={redirectUnauthorized}
      >
        {element}
      </PrivateRoute>
    ),
    ...(children && { children }),
  };
}

export { createPrivateRoute, createRouteBehindFeatureFlag };
