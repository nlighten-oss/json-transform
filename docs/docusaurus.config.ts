import type { Config } from "@docusaurus/types";
import type * as Preset from "@docusaurus/preset-classic";
import MonacoWebpackPlugin from "monaco-editor-webpack-plugin";
import { definitions } from "@nlighten/json-transform-core";

import darkCodeTheme from "./code_theme";
import { generateLanguageFile } from "./plugins/GenerateLanguageFileWebpackPlugin";
import { generateFunctionsMarkdowns } from "./plugins/generateFunctionsMarkdowns";

const icon = "🪄";
const LOGO = `data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 100 100%22><text y=%22.9em%22 font-size=%2290%22>${icon}</text></svg>`;

generateFunctionsMarkdowns();

const config: Config = {
  title: "JSON Transform",
  tagline: "JSON transformation library",
  favicon: LOGO,

  url: "https://nligthen-oss.github.io",
  // Set the /<baseUrl>/ pathname under which your site is served
  // For GitHub pages deployment, it is often '/<projectName>/'
  baseUrl: "/json-transform/",

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: "nlighten-oss", // Usually your GitHub org/user name.
  projectName: "json-transform", // Usually your repo name.
  trailingSlash: false,

  onBrokenLinks: "ignore", //"throw",
  onBrokenMarkdownLinks: "warn",

  // Even if you don't use internationalization, you can use this field to set
  // useful metadata like html lang. For example, if your site is Chinese, you
  // may want to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  plugins: [
    "docusaurus-lunr-search",
    context => {
      return {
        name: "monaco-webpack-plugin-patch",
        configureWebpack(config, isServer, utils) {
          return {
            resolve: {
              fallback: {
                crypto: require.resolve("crypto-browserify"),
                timers: require.resolve("timers-browserify"),
                stream: require.resolve("stream-browserify"),
                buffer: require.resolve("buffer/"),
                fs: false,
                vm: false,
              },
            },
            plugins: [
              new MonacoWebpackPlugin({
                publicPath: "/json-transform",
              }),
              generateLanguageFile(),
            ],
          };
        },
      };
    },
  ],

  presets: [
    [
      "classic",
      {
        docs: {
          // make the docs not be under /docs but /
          routeBasePath: "/",
          sidebarPath: "./sidebars.ts",
          // Please change this to your repo.
          // Remove this to remove the "edit this page" links.
          editUrl: "https://github.com/nlighten-oss/json-transform/tree/main/docs/",
        },
        theme: {
          customCss: "./src/css/custom.css",
        },
      } satisfies Preset.Options,
    ],
  ],

  themeConfig: {
    docs: {
      sidebar: {
        hideable: true,
      },
    },
    navbar: {
      title: "JSON Transform",
      logo: {
        alt: "logo",
        src: LOGO,
      },
      items: [
        {
          to: "playground",
          position: "right",
          label: "Playground",
          className: "button button--primary navbar-button",
        },
        {
          href: "https://github.com/nlighten-oss/json-transform",
          label: "GitHub",
          position: "right",
        },
      ],
    },
    footer: {
      style: "dark",
      links: [
        {
          title: "Docs",
          items: [
            {
              label: "Documentation",
              to: "/json-transform",
            },
            {
              label: "Playground",
              to: "/json-transform/playground",
            },
          ],
        },
        // {
        //   title: 'Community',
        //   items: [
        //     {
        //       label: 'Stack Overflow',
        //       href: 'https://stackoverflow.com/questions/tagged/docusaurus',
        //     },
        //     {
        //       label: 'Discord',
        //       href: 'https://discordapp.com/invite/docusaurus',
        //     },
        //     {
        //       label: 'Twitter',
        //       href: 'https://twitter.com/docusaurus',
        //     },
        //   ],
        // },
        {
          title: "More",
          items: [
            {
              label: "GitHub",
              href: "https://github.com/nlighten-oss/json-transform",
            },
          ],
        },
      ],
      copyright: `Copyright © ${new Date().getFullYear()} nLighten, Inc. Built with Docusaurus.`,
    },
    prism: {
      theme: darkCodeTheme,
      additionalLanguages: ["java"],
    },
  },
  markdown: {
    parseFrontMatter: async params => {
      const result = await params.defaultParseFrontMatter(params);
      if (/\/functions\/[a-z]/.test(params.filePath)) {
        const name = params.filePath.split("/").pop().split(".")[0];
        const definition = definitions[name];
        if (definition) {
          result.frontMatter.title = `$$${name}${definition.aliases ? " / $$" + definition.aliases.join(" / $$") : ""}`;
          result.frontMatter.description = definition.description;
        } else {
          console.warn("No definition found for " + params.filePath);
        }
      }
      return result;
    },
  },
};

export default config;
