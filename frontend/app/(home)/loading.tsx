import { Hero } from "@/features/home/hero";
import { HowItWorks } from "@/features/home/how-it-works";
import { AreaTicketSkeleton, LoadingRegion, SkeletonBar } from "@/components/ui/skeletons";

// Hero e "Como funciona" não dependem da API e já aparecem; só os destinos esperam as 8 áreas.
export default function Loading() {
  return (
    <div className="mx-auto w-full max-w-[1280px] px-5 md:px-10 lg:px-24">
      <Hero />
      <LoadingRegion>
        <div className="flex flex-col items-start gap-3 pt-6 pb-8 md:pb-14">
          <SkeletonBar className="h-4 w-40" />
          <SkeletonBar className="h-9 w-2/3 max-w-[420px] md:h-12" />
          <SkeletonBar className="h-5 w-full max-w-[640px]" />
          <ul
            aria-hidden="true"
            className="grid w-full grid-cols-1 gap-[18px] pt-3 lg:grid-cols-2 md:gap-6 md:pt-5"
          >
            {Array.from({ length: 8 }, (_, index) => (
              <li key={index} className="flex">
                <AreaTicketSkeleton index={index} />
              </li>
            ))}
          </ul>
        </div>
      </LoadingRegion>
      <HowItWorks />
    </div>
  );
}
